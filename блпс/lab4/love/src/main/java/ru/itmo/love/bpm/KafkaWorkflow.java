package ru.itmo.love.bpm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

/** Consumer group distribution and retry/DLT remain in Kafka; orchestration lives in BPMN. */
@Service
@RequiredArgsConstructor
public class KafkaWorkflow {
    private final WorkflowGateway gateway;
    private final ObjectMapper mapper;
    @Transactional
    public void handle(String process, Object event) {
        try {
            gateway.start(process, Map.of("eventJson",mapper.writeValueAsString(event)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) { throw new IllegalArgumentException(e); }
    }
}
