package ru.itmo.love.bpm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandWorkflow {
    private final WorkflowGateway gateway;
    private final ObjectMapper mapper;
    public Object execute(String key, Map<String,Object> input, String result) {
        return gateway.startAndWait(key, input, result);
    }
    public Long create(String key, Object dto) {
        Map<String,Object> input = mapper.convertValue(dto, new com.fasterxml.jackson.core.type.TypeReference<Map<String,Object>>(){});
        input.values().removeIf(Objects::isNull);
        if (key.equals("registerHotel")) {
            for (String field : List.of("id","averageReviewRating","totalReviews")) input.remove(field);
            Object rooms = input.remove("rooms");
            try { if (rooms != null) input.put("roomsJson",mapper.writeValueAsString(rooms)); }
            catch (Exception e) { throw new IllegalArgumentException("Invalid rooms",e); }
        }
        return ((Number)execute(key,input,"resultId")).longValue();
    }
}
