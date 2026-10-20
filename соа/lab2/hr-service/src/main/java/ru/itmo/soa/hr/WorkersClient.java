package ru.itmo.soa.hr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

public final class WorkersClient {
    public record Result(int status, JsonNode body) {}

    private final HttpClient client;
    private final String baseUrl;
    private final String token;
    private final ObjectMapper mapper = new ObjectMapper();

    public WorkersClient(HttpClient client, URI baseUri, String token) {
        boolean loopback = Set.of("localhost", "127.0.0.1", "[::1]").contains(
                baseUri.getHost() == null ? "" : baseUri.getHost());
        if (!("https".equals(baseUri.getScheme()) || ("http".equals(baseUri.getScheme()) && loopback))
                || baseUri.getHost() == null || baseUri.getUserInfo() != null
                || baseUri.getQuery() != null || baseUri.getFragment() != null) {
            throw new IllegalArgumentException("Workers base URL must use HTTPS");
        }
        this.client = client;
        this.baseUrl = baseUri.toString().replaceAll("/+$", "");
        this.token = token;
    }

    public Result indexWorker(long workerId, BigDecimal coefficient) {
        String path = "/workers/" + workerId;
        Result read = exchange("GET", path, null, false);
        if (read.status() != 200 || !(read.body() instanceof ObjectNode worker)) {
            throw unavailable();
        }
        JsonNode salary = worker.get("salary");
        if (salary == null || !salary.isIntegralNumber() || !salary.canConvertToInt() || salary.intValue() <= 0) {
            throw unavailable();
        }
        ObjectNode update = worker.deepCopy();
        update.remove(Set.of("id", "creationDate"));
        update.put("salary", Indexation.salary(salary.intValue(), coefficient));
        return exchange("PUT", path, update, false);
    }

    public Result indexOrganization(long organizationId, BigDecimal coefficient) {
        return exchange("POST", "/internal/organizations/" + organizationId + "/index/" + coefficient,
                null, true);
    }

    private Result exchange(String method, String path, JsonNode body, boolean internal) {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10)).header("Accept", "application/json");
        if (internal) {
            request.header("X-Internal-Token", token);
        }
        if (body != null) {
            request.header("Content-Type", "application/json");
        }
        request.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body.toString()));
        try {
            HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status == 404 || status == 422) {
                throw new ApiException(status, errorMessage(response.body(), status));
            }
            if (status == 204 && !"GET".equals(method)) {
                return new Result(204, null);
            }
            if (status != 200) {
                throw unavailable();
            }
            JsonNode json = mapper.readTree(response.body());
            if (json == null || !json.isObject()) {
                throw unavailable();
            }
            return new Result(200, json);
        } catch (IOException e) {
            throw unavailable();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw unavailable();
        }
    }

    private String errorMessage(String body, int status) {
        try {
            JsonNode json = mapper.readTree(body);
            if (json != null && json.path("message").isTextual() && !json.path("message").asText().isBlank()) {
                return json.path("message").asText();
            }
        } catch (IOException ignored) {
        }
        return status == 404 ? "Worker or organization not found" : "Indexed salary violates constraints";
    }

    private static ApiException unavailable() {
        return new ApiException(503, "Workers service unavailable or returned an invalid response");
    }
}
