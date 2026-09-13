package ru.itmo.love.integration.bitrix.jca;

import jakarta.resource.spi.ConnectionRequestInfo;

/**
 * данные запроса на connection
 */
public record BitrixConnectionRequestInfo(String systemName) implements ConnectionRequestInfo {
}
