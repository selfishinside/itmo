package ru.itmo.love.integration.bitrix.jca;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.resource.ResourceException;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.love.integration.bitrix.BitrixPayload;
import ru.itmo.love.integration.bitrix.BitrixResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * соединение с bitrix
 */
@Slf4j
public class BitrixConnection implements AutoCloseable {

    private final BitrixManagedConnection managedConnection;
    private final String baseUrl;
    private final boolean failCalls;
    private final long responsibleId;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private boolean closed;

    public BitrixConnection(BitrixManagedConnection managedConnection,
                            String baseUrl,
                            boolean failCalls,
                            long responsibleId,
                            HttpClient httpClient,
                            ObjectMapper objectMapper) {
        this.managedConnection = managedConnection;
        this.baseUrl = baseUrl;
        this.failCalls = failCalls;
        this.responsibleId = responsibleId;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /** шлет payload в bitrix */
    public BitrixResult send(BitrixPayload payload) throws ResourceException {
        checkOpen();
        log.info("JCA connection outbound call: operation={}, entityType={}, entityId={}",
                payload.operation(), payload.entityType(), payload.entityId());
        if (failCalls) {
            throw new ResourceException("Bitrix failure requested by test configuration");
        }
        return createBitrixTask(payload);
    }

    /** закрывает handle */
    @Override
    public void close() {
        this.closed = true;
    }

    /** не дает юзать закрытое соединение */
    private void checkOpen() {
        if (closed) {
            throw new IllegalStateException("Bitrix connection is closed");
        }
    }

    /** создает задачу bitrix */
    private BitrixResult createBitrixTask(BitrixPayload payload) throws ResourceException {
        Map<String, Object> fields;
        try {
            fields = createTaskFields(payload);
        } catch (JsonProcessingException exception) {
            throw new ResourceException("Cannot serialize Bitrix task", exception);
        }
        try {
            return sendTaskRequest("tasks.task.add", Map.of("fields", fields));
        } catch (ResourceException exception) {
            if (!canUseLegacyTaskApi(exception)) {
                throw exception;
            }
            log.info("Bitrix modern task api is unavailable, trying legacy task api");
            Map<String, Object> legacyFields = new LinkedHashMap<>(fields);
            legacyFields.remove("CREATED_BY");
            return sendTaskRequest("task.item.add", Map.of("TASKDATA", legacyFields));
        }
    }

    /** шлет запрос в bitrix */
    private BitrixResult sendTaskRequest(String method, Map<String, Object> body) throws ResourceException {
        try {
            String requestBody = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + method))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return parseTaskResponse(response);
        } catch (JsonProcessingException exception) {
            throw new ResourceException("Cannot serialize Bitrix task", exception);
        } catch (IOException exception) {
            throw new ResourceException("Cannot call Bitrix24", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResourceException("Bitrix24 call was interrupted", exception);
        } catch (IllegalArgumentException exception) {
            throw new ResourceException("Invalid Bitrix24 webhook URL", exception);
        }
    }

    /** проверяет можно ли пробовать старый api */
    private boolean canUseLegacyTaskApi(ResourceException exception) {
        String message = exception.getMessage();
        return message != null
                && (message.contains("higher privileges")
                || message.contains("insufficient_scope")
                || message.contains("Method not found"));
    }

    /** собирает поля задачи */
    private Map<String, Object> createTaskFields(BitrixPayload payload) throws JsonProcessingException {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("TITLE", createTaskTitle(payload));
        fields.put("DESCRIPTION", "Источник: hotel booking application\n"
                + "Тип сущности: " + payload.entityType() + "\n"
                + "ID сущности: " + payload.entityId() + "\n"
                + "Операция: " + payload.operation() + "\n"
                + "Данные: " + objectMapper.writeValueAsString(payload.data()));
        fields.put("CREATED_BY", responsibleId);
        fields.put("RESPONSIBLE_ID", responsibleId);
        return fields;
    }

    /** собирает название задачи */
    private String createTaskTitle(BitrixPayload payload) {
        return switch (payload.operation()) {
            case "PUBLISH_REVIEW" -> "Проверить опубликованный отзыв #" + payload.entityId();
            case "SEND_NOTIFICATION" -> "Проверить уведомление гостю #" + payload.entityId();
            default -> "Обработать " + payload.entityType() + " #" + payload.entityId();
        };
    }

    /** достает id задачи */
    private BitrixResult parseTaskResponse(HttpResponse<String> response) throws ResourceException {
        try {
            JsonNode body = objectMapper.readTree(response.body());
            if (response.statusCode() < 200 || response.statusCode() >= 300 || body.hasNonNull("error")) {
                String error = body.path("error_description").asText(body.path("error").asText(response.body()));
                throw new ResourceException("Bitrix24 rejected task: " + error);
            }
            JsonNode result = body.path("result");
            String taskId = result.path("task").path("id").asText();
            if (taskId.isBlank()) {
                taskId = result.path("item").path("id").asText();
            }
            if (taskId.isBlank()) {
                taskId = result.path("id").asText();
            }
            if (taskId.isBlank() && result.isValueNode()) {
                taskId = result.asText();
            }
            if (taskId.isBlank()) {
                throw new ResourceException("Bitrix24 response does not contain task id: " + response.body());
            }
            return new BitrixResult(true, taskId, response.body());
        } catch (JsonProcessingException exception) {
            throw new ResourceException("Cannot parse Bitrix24 response", exception);
        }
    }
}
