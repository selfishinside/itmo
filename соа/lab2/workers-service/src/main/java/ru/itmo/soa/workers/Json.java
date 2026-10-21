package ru.itmo.soa.workers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

final class Json {
    static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);

    private Json() { }

    static ObjectNode parse(String body, int shapeStatus) {
        if (body == null || body.isBlank()) throw new ApiException(400, "Missing JSON body");
        try {
            JsonNode node = MAPPER.readTree(body == null ? "" : body);
            if (node == null || !node.isObject()) {
                throw new ApiException(shapeStatus, "Expected a JSON object");
            }
            return (ObjectNode) node;
        } catch (JsonProcessingException e) {
            throw new ApiException(400, "Malformed JSON");
        }
    }

    static Response response(int status, Object value) {
        try {
            return Response.status(status).type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(MAPPER.writeValueAsString(value)).build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize response", e);
        }
    }

    static ObjectNode error(int status, String message) {
        return MAPPER.createObjectNode().put("status", status).put("message", message);
    }
}
