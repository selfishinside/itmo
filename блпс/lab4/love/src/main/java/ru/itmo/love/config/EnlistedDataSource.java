package ru.itmo.love.config;

import jakarta.transaction.*;
import org.springframework.jdbc.datasource.AbstractDataSource;
import javax.sql.*;
import java.sql.*;
import java.lang.reflect.*;
import java.util.concurrent.ConcurrentHashMap;

/** One XA connection per JTA transaction, shared by Hibernate and Camunda JDBC. */
public final class EnlistedDataSource extends AbstractDataSource {
    private final XADataSource source;
    private final TransactionManager manager;
    private final ConcurrentHashMap<Transaction, Connection> connections = new ConcurrentHashMap<>();

    public EnlistedDataSource(XADataSource source, TransactionManager manager) {
        this.source = source;
        this.manager = manager;
    }

    @Override public Connection getConnection() throws SQLException {
        try {
            Transaction tx = manager.getTransaction();
            if (tx == null) {
                XAConnection xa = source.getXAConnection();
                return proxy(xa.getConnection(), xa, false);
            }
            synchronized (connections) {
                Connection existing = connections.get(tx);
                if (existing != null) return existing;
                XAConnection xa = source.getXAConnection();
                try {
                    Connection physical = xa.getConnection();
                    if (!tx.enlistResource(xa.getXAResource())) throw new SQLException("XA enlistment failed");
                    Connection connection = proxy(physical, xa, true);
                    tx.registerSynchronization(new Synchronization() {
                        public void beforeCompletion() { }
                        public void afterCompletion(int status) {
                            connections.remove(tx);
                            try { physical.close(); xa.close(); }
                            catch (SQLException e) { logger.warn("Closing XA connection failed", e); }
                        }
                    });
                    connections.put(tx, connection);
                    return connection;
                } catch (Exception e) { xa.close(); throw e; }
            }
        } catch (Exception e) { throw new SQLException("Cannot obtain JTA connection", e); }
    }

    @Override public Connection getConnection(String user, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException("Use configured datasource credentials");
    }

    private Connection proxy(Connection physical, XAConnection xa, boolean managed) {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
            new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                String name = method.getName();
                if (name.equals("close")) {
                    if (!managed) { physical.close(); xa.close(); }
                    return null;
                }
                if (managed && (name.equals("commit") || name.equals("rollback") || name.equals("setAutoCommit"))) {
                    throw new SQLException("Transaction controlled by Narayana");
                }
                try { return method.invoke(physical, args); }
                catch (InvocationTargetException e) { throw e.getCause(); }
            });
    }
}
