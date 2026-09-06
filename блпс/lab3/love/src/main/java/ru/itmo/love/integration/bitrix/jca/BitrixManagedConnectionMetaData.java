package ru.itmo.love.integration.bitrix.jca;

import jakarta.resource.spi.ManagedConnectionMetaData;

/**
 * metadata bitrix connection
 */
public class BitrixManagedConnectionMetaData implements ManagedConnectionMetaData {

    /** имя eis */
    @Override
    public String getEISProductName() {
        return "Bitrix24";
    }

    /** версия eis */
    @Override
    public String getEISProductVersion() {
        return "REST webhook";
    }

    /** лимита тут нет */
    @Override
    public int getMaxConnections() {
        return 0;
    }

    /** юзер webhook */
    @Override
    public String getUserName() {
        return "bitrix-webhook";
    }
}
