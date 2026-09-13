package ru.itmo.love.integration.bitrix.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;

/**
 * фабрика соединений bitrix
 */
public class BitrixConnectionFactory {

    private final BitrixManagedConnectionFactory managedConnectionFactory;
    private final ConnectionManager connectionManager;

    public BitrixConnectionFactory(BitrixManagedConnectionFactory managedConnectionFactory,
                                   ConnectionManager connectionManager) {
        this.managedConnectionFactory = managedConnectionFactory;
        this.connectionManager = connectionManager;
    }

    /** берет connection через manager */
    public BitrixConnection getConnection() {
        try {
            return (BitrixConnection) connectionManager.allocateConnection(
                    managedConnectionFactory,
                    new BitrixConnectionRequestInfo("BITRIX24")
            );
        } catch (ResourceException exception) {
            throw new IllegalStateException("Cannot allocate Bitrix JCA connection", exception);
        }
    }
}
