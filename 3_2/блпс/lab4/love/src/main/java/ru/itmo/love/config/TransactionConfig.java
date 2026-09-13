package ru.itmo.love.config;

import com.arjuna.ats.jta.UserTransaction;
import jakarta.transaction.TransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** jta конфигурация */
@Configuration
public class TransactionConfig {

    @Bean
    public javax.sql.DataSource dataSource(org.springframework.core.env.Environment env,
                                           TransactionManager manager) throws Exception {
        String url = env.getRequiredProperty("spring.datasource.url");
        String className = url.startsWith("jdbc:h2:") ? "org.h2.jdbcx.JdbcDataSource" : "org.postgresql.xa.PGXADataSource";
        javax.sql.XADataSource xa = (javax.sql.XADataSource) Class.forName(className).getConstructor().newInstance();
        Class<?> type = xa.getClass();
        type.getMethod("setURL", String.class).invoke(xa, url);
        type.getMethod("setUser", String.class).invoke(xa, env.getRequiredProperty("spring.datasource.username"));
        type.getMethod("setPassword", String.class).invoke(xa, env.getProperty("spring.datasource.password", ""));
        return new EnlistedDataSource(xa, manager);
    }

    /** создает usertransaction narayana */
    @Bean
    public jakarta.transaction.UserTransaction narayanaUserTransaction() {
        return UserTransaction.userTransaction();
    }

    /** создает transactionmanager narayana */
    @Bean
    public TransactionManager narayanaTransactionManager() {
        return com.arjuna.ats.jta.TransactionManager.transactionManager();
    }

    /** создает менеджер транзакций spring */
    @Bean
    public PlatformTransactionManager transactionManager(
            jakarta.transaction.UserTransaction userTransaction,
            TransactionManager transactionManager
    ) {
        return new JtaTransactionManager(userTransaction, transactionManager);
    }

    /** создает шаблон транзакций */
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setName("love-jta-transaction");
        return transactionTemplate;
    }
}
