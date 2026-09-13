package ru.itmo.love.service;

import ru.itmo.love.entity.IntegrationLog;
import ru.itmo.love.integration.bitrix.BitrixPayload;
import ru.itmo.love.integration.bitrix.BitrixResult;

import java.util.List;

/**
 * контракт bitrix
 */
public interface BitrixConnectorInt {

    /** шлет данные в bitrix */
    BitrixResult send(BitrixPayload payload);

    /** отдает логи */
    List<IntegrationLog> getLogs();
}
