package ru.itmo.love.service;

import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.love.entity.IntegrationLog;
import ru.itmo.love.integration.bitrix.BitrixPayload;
import ru.itmo.love.integration.bitrix.BitrixResult;
import ru.itmo.love.integration.bitrix.jca.BitrixConnection;
import ru.itmo.love.integration.bitrix.jca.BitrixConnectionFactory;
import ru.itmo.love.repository.IntegrationLogRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * сервис bitrix
 */
@Service
@RequiredArgsConstructor
public class BitrixConnector implements BitrixConnectorInt {

    private final BitrixConnectionFactory connectionFactory;
    private final IntegrationLogRepository integrationLogRepository;

    /** шлет в bitrix и пишет лог */
    @Override
    @Transactional
    public BitrixResult send(BitrixPayload payload) {
        try (BitrixConnection connection = connectionFactory.getConnection()) {
            BitrixResult result = connection.send(payload);
            saveLog(payload, "SUCCESS", result.externalId(), result.response(), null);
            return result;
        } catch (ResourceException exception) {
            saveLog(payload, "FAILED", null, null, exception.getMessage());
            return new BitrixResult(false, null, exception.getMessage());
        }
    }

    /** отдает логи интеграции */
    @Override
    @Transactional(readOnly = true)
    public List<IntegrationLog> getLogs() {
        return integrationLogRepository.findAll();
    }

    /** сохраняет лог */
    private void saveLog(BitrixPayload payload, String status, String externalId, String response, String error) {
        integrationLogRepository.save(IntegrationLog.builder()
                .entityType(payload.entityType())
                .entityId(payload.entityId())
                .operation(payload.operation())
                .status(status)
                .externalId(externalId)
                .bitrixResponse(response)
                .errorMessage(error)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
