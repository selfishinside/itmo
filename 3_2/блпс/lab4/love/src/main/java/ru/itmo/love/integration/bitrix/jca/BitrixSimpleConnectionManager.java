package ru.itmo.love.integration.bitrix.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnectionFactory;

import javax.security.auth.Subject;

/**
 * простой manager без wildfly
 */
public class BitrixSimpleConnectionManager implements ConnectionManager {

    /** сразу отдает handle */
    @Override
    public Object allocateConnection(ManagedConnectionFactory mcf, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        Subject subject = null;
        return mcf.createManagedConnection(subject, cxRequestInfo)
                .getConnection(subject, cxRequestInfo);
    }
}
