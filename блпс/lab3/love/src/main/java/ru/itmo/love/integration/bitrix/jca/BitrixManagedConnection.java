package ru.itmo.love.integration.bitrix.jca;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.net.http.HttpClient;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * managed connection для bitrix
 */
public class BitrixManagedConnection implements ManagedConnection {

    private final String baseUrl;
    private final boolean failCalls;
    private final long responsibleId;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Set<ConnectionEventListener> listeners = new CopyOnWriteArraySet<>();
    private PrintWriter logWriter;

    public BitrixManagedConnection(String baseUrl,
                                   boolean failCalls,
                                   long responsibleId,
                                   HttpClient httpClient,
                                   ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.failCalls = failCalls;
        this.responsibleId = responsibleId;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /** выдает handle сервису */
    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) {
        return new BitrixConnection(this, baseUrl, failCalls, responsibleId, httpClient, objectMapper);
    }

    /** чистит listeners */
    @Override
    public void destroy() {
        listeners.clear();
    }

    /** чистит состояние */
    @Override
    public void cleanup() {
        // состояния нет
    }

    /** проверяет handle */
    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (!(connection instanceof BitrixConnection)) {
            throw new ResourceException("Unsupported Bitrix connection handle: " + connection);
        }
    }

    /** добавляет listener */
    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    /** убирает listener */
    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    /** xa тут нет */
    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new ResourceException("Bitrix24 REST integration does not support XA transactions");
    }

    /** локальных транзакций тут нет */
    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new ResourceException("Bitrix24 REST integration does not support local transactions");
    }

    /** отдает metadata */
    @Override
    public ManagedConnectionMetaData getMetaData() {
        return new BitrixManagedConnectionMetaData();
    }

    /** ставит log writer */
    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    /** отдает log writer */
    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

}
