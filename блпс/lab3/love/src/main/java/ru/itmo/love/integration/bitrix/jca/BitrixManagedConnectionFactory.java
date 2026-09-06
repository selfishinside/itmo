package ru.itmo.love.integration.bitrix.jca;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Set;

/**
 * фабрика managed connection
 */
public class BitrixManagedConnectionFactory implements ManagedConnectionFactory {

    private final String baseUrl;
    private final boolean failCalls;
    private final long responsibleId;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private PrintWriter logWriter;

    public BitrixManagedConnectionFactory(String baseUrl, boolean failCalls, long responsibleId) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.failCalls = failCalls;
        this.responsibleId = responsibleId;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /** создает factory с manager */
    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        return new BitrixConnectionFactory(this, cxManager);
    }

    /** создает factory без сервера приложений */
    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new BitrixConnectionFactory(this, new BitrixSimpleConnectionManager());
    }

    /** создает managed connection */
    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        return new BitrixManagedConnection(baseUrl, failCalls, responsibleId, httpClient, objectMapper);
    }

    /** ищет готовое connection */
    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet,
                                                     Subject subject,
                                                     ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        if (connectionSet == null || connectionSet.isEmpty()) {
            return null;
        }
        for (Object candidate : connectionSet) {
            if (candidate instanceof BitrixManagedConnection managedConnection) {
                return managedConnection;
            }
        }
        return null;
    }

    /** ставит log writer */
    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    /** отдает log writer */
    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }
}
