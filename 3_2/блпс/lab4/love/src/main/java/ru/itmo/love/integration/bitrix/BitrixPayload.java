package ru.itmo.love.integration.bitrix;

import java.util.Map;

/**
 * payload для bitrix
 */
public record BitrixPayload(String entityType, Long entityId, String operation, Map<String, Object> data) {
}
