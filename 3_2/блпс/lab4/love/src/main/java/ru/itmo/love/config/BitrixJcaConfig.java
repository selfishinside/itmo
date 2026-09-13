package ru.itmo.love.config;

import jakarta.resource.ResourceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itmo.love.integration.bitrix.jca.BitrixConnectionFactory;
import ru.itmo.love.integration.bitrix.jca.BitrixManagedConnectionFactory;

/**
 * собирает bitrix connector
 */
@Configuration
public class BitrixJcaConfig {

    /** хранит настройки bitrix */
    @Bean
    public BitrixManagedConnectionFactory bitrixManagedConnectionFactory(
            @Value("${app.bitrix.base-url}") String baseUrl,
            @Value("${app.bitrix.fail-next-call:false}") boolean failCalls,
            @Value("${app.bitrix.responsible-id:1}") long responsibleId
    ) {
        return new BitrixManagedConnectionFactory(baseUrl, failCalls, responsibleId);
    }

    /** дает сервисам connection factory */
    @Bean
    public BitrixConnectionFactory bitrixConnectionFactory(BitrixManagedConnectionFactory managedConnectionFactory) {
        try {
            return (BitrixConnectionFactory) managedConnectionFactory.createConnectionFactory();
        } catch (ResourceException exception) {
            throw new IllegalStateException("Cannot create Bitrix JCA connection factory", exception);
        }
    }
}
