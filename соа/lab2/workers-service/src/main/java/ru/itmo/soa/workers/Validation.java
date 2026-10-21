package ru.itmo.soa.workers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class Validation {
    static final List<String> POSITIONS = List.of("MANAGER", "HEAD_OF_DIVISION", "DEVELOPER", "LEAD_DEVELOPER");
    static final Map<String, List<String>> ENUMS = Map.of(
            "position", POSITIONS,
            "status", List.of("FIRED", "RECOMMENDED_FOR_PROMOTION", "REGULAR", "PROBATION"),
            "person.eyeColor", List.of("RED", "BLACK", "YELLOW", "ORANGE", "WHITE"),
            "person.nationality", List.of("FRANCE", "THAILAND", "NORTH_KOREA"));

    private Validation() { }

    static void require(boolean condition, int status, String message) {
        if (!condition) throw new ApiException(status, message);
    }

    static void fields(ObjectNode node, Set<String> allowed, int status) {
        node.fieldNames().forEachRemaining(name -> require(allowed.contains(name), status, "Unknown field: " + name));
    }

    static long integer(JsonNode value, long min, long max, int status, String name) {
        require(value != null && value.isIntegralNumber() && value.canConvertToLong(), status, name + " must be an integer");
        long n = value.longValue();
        require(n >= min && n <= max, status, name + " is out of range");
        return n;
    }

    static long pathInteger(String value, long min, long max, int status, String name) {
        require(value != null && value.matches("-?[0-9]+"), status, name + " must be an integer");
        try {
            long n = Long.parseLong(value);
            require(n >= min && n <= max, status, name + " is out of range");
            return n;
        } catch (NumberFormatException e) {
            throw new ApiException(status, name + " is out of range");
        }
    }

    static double decimal(JsonNode value, int status, String name) {
        require(value != null && value.isNumber() && Double.isFinite(value.doubleValue()),
                status, name + " must be a finite number");
        return value.doubleValue();
    }

    static String text(JsonNode value, int status, String name) {
        require(value != null && value.isTextual(), status, name + " must be a string");
        return value.textValue();
    }

    static void date(JsonNode value, int status, String name) {
        String s = text(value, status, name);
        require(s.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"), status, name + " must use YYYY-MM-DD");
        try { LocalDate.parse(s); }
        catch (DateTimeParseException e) { throw new ApiException(status, name + " is not a valid date"); }
    }

    static void enumValue(JsonNode value, String field, int status) {
        require(value != null && value.isTextual() && ENUMS.get(field).contains(value.textValue()), status,
                "Invalid value for " + field);
    }

    static ObjectNode worker(ObjectNode input) {
        int code = 422;
        fields(input, Set.of("id", "name", "coordinates", "creationDate", "salary", "position", "status", "person"), code);
        ObjectNode w = input.deepCopy();
        if (w.has("id")) integer(w.get("id"), 1, Long.MAX_VALUE, code, "id");
        if (w.has("creationDate")) date(w.get("creationDate"), code, "creationDate");
        require(!text(w.get("name"), code, "name").isBlank(), code, "name must not be empty");
        integer(w.get("salary"), 1, Integer.MAX_VALUE, code, "salary");
        require(w.path("coordinates").isObject(), code, "coordinates must be an object");
        ObjectNode coordinates = (ObjectNode) w.get("coordinates");
        fields(coordinates, Set.of("x", "y"), code);
        integer(coordinates.get("x"), Integer.MIN_VALUE, 225, code, "coordinates.x");
        decimal(coordinates.get("y"), code, "coordinates.y");
        for (String field : List.of("position", "status")) {
            if (w.hasNonNull(field)) enumValue(w.get(field), field, code);
            else w.putNull(field);
        }
        if (w.hasNonNull("person")) {
            require(w.get("person").isObject(), code, "person must be an object");
            ObjectNode person = (ObjectNode) w.get("person");
            fields(person, Set.of("passportID", "eyeColor", "nationality"), code);
            text(person.get("passportID"), code, "person.passportID");
            for (String field : List.of("eyeColor", "nationality")) {
                if (person.hasNonNull(field)) enumValue(person.get(field), "person." + field, code);
                else person.putNull(field);
            }
        } else w.putNull("person");
        return w;
    }
}
