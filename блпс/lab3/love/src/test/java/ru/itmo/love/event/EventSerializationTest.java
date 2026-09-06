package ru.itmo.love.event;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Проверяет JSON-сериализацию доменных событий, передаваемых через Kafka.
 */
class EventSerializationTest {

    /** Проверяет, что BookingConfirmedEvent сохраняет данные после JSON round-trip. */
    @Test
    void bookingConfirmedEventCanBeSerialized() throws Exception {
        var mapper = JsonMapper.builder().findAndAddModules().build();
        var source = new BookingConfirmedEvent(
                "event-1", 10L, 20L, "guest@example.com", "Hotel", LocalDateTime.now());

        String json = mapper.writeValueAsString(source);
        BookingConfirmedEvent restored = mapper.readValue(json, BookingConfirmedEvent.class);

        assertEquals(source, restored);
    }
}
