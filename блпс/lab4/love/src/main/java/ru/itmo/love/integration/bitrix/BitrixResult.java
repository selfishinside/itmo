package ru.itmo.love.integration.bitrix;

/**
 * ответ bitrix
 */
public record BitrixResult(boolean success, String externalId, String response) {
}
