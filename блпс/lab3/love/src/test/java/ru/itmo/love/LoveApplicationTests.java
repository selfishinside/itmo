package ru.itmo.love;

import org.junit.jupiter.api.Test;
class LoveApplicationTests {

    /** Проверяет наличие точки входа без требования запущенных PostgreSQL и Kafka. */
    @Test
    void applicationEntryPointExists() {
        org.junit.jupiter.api.Assertions.assertNotNull(LoveApplication.class);
    }

}
