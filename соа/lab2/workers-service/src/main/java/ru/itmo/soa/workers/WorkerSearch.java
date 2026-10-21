package ru.itmo.soa.workers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class WorkerSearch {
    private static final Map<String, String> ARRAYS = Map.of("position", "position", "status", "status",
            "personEyeColor", "person.eyeColor", "personNationality", "person.nationality");
    private static final Map<String, String> RANGES = Map.of(
            "salary", "salary", "coordinatesX", "coordinates.x", "coordinatesY", "coordinates.y");
    private static final Set<String> SORT_FIELDS = Set.of("id", "name", "salary", "creationDate",
            "coordinates.x", "coordinates.y", "position", "status", "person.passportID", "person.eyeColor", "person.nationality");
    private static final Set<String> FIELDS;
    static {
        Set<String> fields = new HashSet<>(List.of("id", "name", "personPassportID", "creationDateFrom",
                "creationDateTo", "page", "size", "sort"));
        fields.addAll(ARRAYS.keySet());
        RANGES.keySet().forEach(k -> { fields.add(k + "Min"); fields.add(k + "Max"); });
        FIELDS = Set.copyOf(fields);
    }

    private final ObjectNode filter;
    private final int page;
    private final int size;
    private final List<String> sort = new ArrayList<>();

    WorkerSearch(ObjectNode input) {
        filter = input.deepCopy();
        Validation.fields(filter, FIELDS, 400);
        page = (int) number("page", 1, Integer.MAX_VALUE, 1);
        size = (int) number("size", 1, 100, 20);
        number("id", 1, Long.MAX_VALUE, 1);
        for (String prefix : RANGES.keySet()) {
            if (prefix.equals("coordinatesY")) {
                double lo = filter.has("coordinatesYMin") ? Validation.decimal(filter.get("coordinatesYMin"), 400, "coordinatesYMin") : -Double.MAX_VALUE;
                double hi = filter.has("coordinatesYMax") ? Validation.decimal(filter.get("coordinatesYMax"), 400, "coordinatesYMax") : Double.MAX_VALUE;
                Validation.require(lo <= hi, 400, "Invalid coordinatesY range");
                continue;
            }
            long min = prefix.equals("salary") ? 1 : Integer.MIN_VALUE;
            long max = prefix.equals("coordinatesX") ? 225 : Integer.MAX_VALUE;
            long lo = number(prefix + "Min", min, max, min);
            long hi = number(prefix + "Max", min, max, max);
            Validation.require(lo <= hi, 400, prefix + "Min must not exceed Max");
        }
        for (String field : List.of("name", "personPassportID")) {
            if (filter.has(field)) Validation.text(filter.get(field), 400, field);
        }
        for (String field : List.of("creationDateFrom", "creationDateTo")) {
            if (filter.has(field)) Validation.date(filter.get(field), 400, field);
        }
        if (filter.has("creationDateFrom") && filter.has("creationDateTo")) {
            Validation.require(filter.get("creationDateFrom").textValue().compareTo(filter.get("creationDateTo").textValue()) <= 0,
                    400, "Invalid date range");
        }
        ARRAYS.forEach((field, path) -> {
            if (filter.has(field)) {
                Validation.require(filter.get(field).isArray(), 400, field + " must be an array");
                filter.get(field).forEach(v -> Validation.enumValue(v, path, 400));
            }
        });
        if (filter.has("sort")) {
            Validation.require(filter.get("sort").isArray(), 400, "sort must be an array");
            filter.get("sort").forEach(v -> {
                String field = Validation.text(v, 400, "sort entry");
                Validation.require(SORT_FIELDS.contains(field.startsWith("-") ? field.substring(1) : field), 400, "Invalid sort field: " + field);
                sort.add(field);
            });
        }
    }

    static ObjectNode query(MultivaluedMap<String, String> query) {
        ObjectNode result = Json.MAPPER.createObjectNode();
        if (query.containsKey("search")) {
            Validation.require(query.keySet().stream().allMatch(Set.of("search", "page", "size")::contains)
                    && query.get("search").size() == 1, 400, "Invalid search parameters");
            ObjectNode search = Json.parse(query.getFirst("search"), 400);
            for (String key : List.of("page", "size")) {
                if (query.containsKey(key)) {
                    Validation.require(query.get(key).size() == 1, 400, "Repeated query parameter: " + key);
                    search.put(key, Validation.pathInteger(query.getFirst(key), 1, Integer.MAX_VALUE, 400, key));
                }
            }
            return search;
        }
        query.forEach((key, values) -> {
            Validation.require(Set.of("page", "size").contains(key), 400, "Unknown query parameter: " + key);
            Validation.require(values.size() == 1, 400, "Repeated query parameter: " + key);
            result.put(key, Validation.pathInteger(values.get(0), 1, Integer.MAX_VALUE, 400, key));
        });
        return result;
    }

    private long number(String key, long min, long max, long fallback) {
        return filter.has(key) ? Validation.integer(filter.get(key), min, max, 400, key) : fallback;
    }

    private static JsonNode at(JsonNode node, String path) {
        for (String part : path.split("\\.")) node = node.path(part);
        return node;
    }

    private boolean matches(ObjectNode w) {
        for (String key : List.of("id")) {
            if (filter.has(key) && filter.get(key).longValue() != w.path(key).longValue()) return false;
        }
        if (filter.has("name") && !w.path("name").textValue().contains(filter.get("name").textValue())) return false;
        if (filter.has("personPassportID") && (!w.path("person").isObject()
                || !w.path("person").path("passportID").asText().equals(filter.get("personPassportID").textValue()))) return false;
        for (var entry : RANGES.entrySet()) {
            var value = at(w, entry.getValue()).decimalValue();
            if (filter.has(entry.getKey() + "Min") && value.compareTo(filter.get(entry.getKey() + "Min").decimalValue()) < 0) return false;
            if (filter.has(entry.getKey() + "Max") && value.compareTo(filter.get(entry.getKey() + "Max").decimalValue()) > 0) return false;
        }
        String date = w.path("creationDate").textValue();
        if (filter.has("creationDateFrom") && date.compareTo(filter.get("creationDateFrom").textValue()) < 0) return false;
        if (filter.has("creationDateTo") && date.compareTo(filter.get("creationDateTo").textValue()) > 0) return false;
        for (var entry : ARRAYS.entrySet()) {
            if (!filter.has(entry.getKey())) continue;
            JsonNode values = filter.get(entry.getKey());
            JsonNode actual = at(w, entry.getValue());
            if (!values.isEmpty()) {
                boolean found = false;
                for (JsonNode value : values) if (value.equals(actual)) found = true;
                if (!found) return false;
            }
        }
        return true;
    }

    private int compare(ObjectNode a, ObjectNode b, String spec) {
        boolean descending = spec.startsWith("-");
        String field = descending ? spec.substring(1) : spec;
        JsonNode x = at(a, field), y = at(b, field);
        boolean xn = x.isNull() || x.isMissingNode(), yn = y.isNull() || y.isMissingNode();
        if (xn || yn) return xn == yn ? 0 : xn ? 1 : -1;
        int result;
        if (Validation.ENUMS.containsKey(field)) {
            List<String> order = Validation.ENUMS.get(field);
            result = Integer.compare(order.indexOf(x.textValue()), order.indexOf(y.textValue()));
        } else if (x.isNumber()) result = x.decimalValue().compareTo(y.decimalValue());
        else result = x.textValue().compareTo(y.textValue());
        return descending ? -result : result;
    }

    ObjectNode apply(List<ObjectNode> workers) {
        Comparator<ObjectNode> comparator = (a, b) -> {
            for (String field : sort) {
                int c = compare(a, b, field);
                if (c != 0) return c;
            }
            return Long.compare(a.path("id").longValue(), b.path("id").longValue());
        };
        List<ObjectNode> matches = workers.stream().filter(this::matches).sorted(comparator).toList();
        ObjectNode result = Json.MAPPER.createObjectNode().put("page", page).put("size", size)
                .put("totalElements", (long) matches.size()).put("totalPages", (matches.size() + (long) size - 1) / size);
        ArrayNode content = result.putArray("content");
        long offset = ((long) page - 1) * size;
        matches.stream().skip(offset).limit(size).forEach(w -> content.add(w.deepCopy()));
        return result;
    }
}
