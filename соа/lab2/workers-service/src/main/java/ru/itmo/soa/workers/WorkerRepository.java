package ru.itmo.soa.workers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class WorkerRepository {
    private final Path file;
    private Map<Long, ObjectNode> workers = new LinkedHashMap<>();
    private Set<Long> organizations = new HashSet<>(Set.of(1L));
    private Map<Long, Long> memberships = new LinkedHashMap<>();
    private long lastId;

    public WorkerRepository(Path file) {
        this.file = file.toAbsolutePath();
        load();
    }

    public static WorkerRepository configured() {
        return new WorkerRepository(Path.of(System.getProperty("workers.data.file",
                Path.of(System.getProperty("user.home"), ".soa-lab2", "workers.json").toString())));
    }

    private void load() {
        if (!Files.exists(file)) return;
        try {
            JsonNode root = Json.MAPPER.readTree(Files.readAllBytes(file));
            Validation.require(root != null && root.isObject() && root.path("workers").isArray()
                    && root.path("organizations").isArray(), 500, "Invalid repository document");
            lastId = Validation.integer(root.get("lastId"), 0, Long.MAX_VALUE, 500, "lastId");
            int version = root.path("version").asInt(1);
            Validation.require(version == 1 || version == 2, 500, "Unsupported repository version");
            for (JsonNode org : root.get("organizations")) organizations.add(Validation.integer(org, 1, Long.MAX_VALUE, 500, "orgId"));
            for (JsonNode node : root.get("workers")) {
                Validation.require(node.isObject(), 500, "Invalid stored worker");
                ObjectNode input = ((ObjectNode) node).deepCopy();
                long organization = version == 1 && input.has("orgId")
                        ? Validation.integer(input.get("orgId"), 1, Long.MAX_VALUE, 500, "orgId") : 1;
                if (version == 1) input.remove("orgId");
                ObjectNode worker = Validation.worker(input);
                long id = Validation.integer(worker.get("id"), 1, Long.MAX_VALUE, 500, "id");
                Validation.date(worker.get("creationDate"), 500, "creationDate");
                Validation.require(workers.put(id, worker) == null && id <= lastId, 500, "Invalid stored worker id");
                if (version == 1) {
                    memberships.put(id, organization);
                    organizations.add(organization);
                }
            }
            if (version == 2) {
                Validation.require(root.path("memberships").isArray(), 500, "Missing organization memberships");
                for (JsonNode link : root.get("memberships")) {
                    long workerId = Validation.integer(link.get("workerId"), 1, Long.MAX_VALUE, 500, "workerId");
                    long organizationId = Validation.integer(link.get("organizationId"), 1, Long.MAX_VALUE, 500, "organizationId");
                    Validation.require(workers.containsKey(workerId) && organizations.contains(organizationId)
                            && memberships.put(workerId, organizationId) == null, 500, "Invalid organization membership");
                }
                Validation.require(memberships.size() == workers.size(), 500, "Every worker must belong to one organization");
            } else {
                Path backup = file.resolveSibling(file.getFileName() + ".v1.bak");
                if (!Files.exists(backup)) Files.copy(file, backup);
                commit(workers, organizations, memberships, lastId);
            }
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Cannot load workers repository: " + file, e);
        }
    }

    private void commit(Map<Long, ObjectNode> next, Set<Long> known, Map<Long, Long> links, long nextLastId) {
        ObjectNode root = Json.MAPPER.createObjectNode().put("version", 2).put("lastId", nextLastId);
        var orgs = root.putArray("organizations");
        known.stream().sorted().forEach(orgs::add);
        var rows = root.putArray("workers");
        next.values().forEach(rows::add);
        var relations = root.putArray("memberships");
        links.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(link ->
                relations.addObject().put("workerId", link.getKey()).put("organizationId", link.getValue()));
        Path temporary = null;
        try {
            Files.createDirectories(file.getParent());
            temporary = Files.createTempFile(file.getParent(), ".workers-", ".tmp");
            byte[] bytes = Json.MAPPER.writeValueAsBytes(root);
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) channel.write(buffer);
                channel.force(true);
            }
            // Replace the file atomically.
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            workers = next;
            organizations = known;
            memberships = links;
            lastId = nextLastId;
        } catch (IOException e) {
            throw new ApiException(500, "Could not persist workers");
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (IOException ignored) { /* Cleanup only. */ }
            }
        }
    }

    public synchronized ObjectNode create(ObjectNode input) {
        ObjectNode worker = writableWorker(input);
        Validation.require(lastId != Long.MAX_VALUE, 422, "Worker id space exhausted");
        long id = lastId + 1;
        worker.put("id", id).put("creationDate", LocalDate.now(ZoneOffset.UTC).toString());
        Map<Long, ObjectNode> next = new LinkedHashMap<>(workers);
        next.put(id, worker);
        Set<Long> known = new HashSet<>(organizations);
        Map<Long, Long> links = new LinkedHashMap<>(memberships);
        links.put(id, 1L);
        commit(next, known, links, id);
        return worker.deepCopy();
    }

    public synchronized ObjectNode get(long id) {
        ObjectNode worker = workers.get(id);
        if (worker == null) throw new ApiException(404, "Worker not found");
        return worker.deepCopy();
    }

    public synchronized ObjectNode update(long id, ObjectNode input) {
        ObjectNode previous = get(id);
        ObjectNode worker = writableWorker(input);
        worker.put("id", id).set("creationDate", previous.get("creationDate"));
        Map<Long, ObjectNode> next = new LinkedHashMap<>(workers);
        next.put(id, worker);
        Set<Long> known = new HashSet<>(organizations);
        commit(next, known, new LinkedHashMap<>(memberships), lastId);
        return worker.deepCopy();
    }

    public synchronized void delete(long id) {
        get(id);
        Map<Long, ObjectNode> next = new LinkedHashMap<>(workers);
        next.remove(id);
        Map<Long, Long> links = new LinkedHashMap<>(memberships);
        links.remove(id);
        commit(next, new HashSet<>(organizations), links, lastId);
    }

    public synchronized com.fasterxml.jackson.databind.node.ArrayNode organizations() {
        var result = Json.MAPPER.createArrayNode();
        organizations.stream().sorted().forEach(id -> {
            var members = result.addObject().put("id", id).putArray("workerIds");
            memberships.entrySet().stream().filter(link -> link.getValue().equals(id))
                    .map(Map.Entry::getKey).sorted().forEach(members::add);
        });
        return result;
    }

    public synchronized void assign(long organizationId, long workerId) {
        Validation.require(organizationId > 0, 400, "organizationId must be positive");
        get(workerId);
        if (Long.valueOf(organizationId).equals(memberships.get(workerId))) return;
        Set<Long> known = new HashSet<>(organizations);
        known.add(organizationId);
        Map<Long, Long> links = new LinkedHashMap<>(memberships);
        links.put(workerId, organizationId);
        commit(new LinkedHashMap<>(workers), known, links, lastId);
    }

    public synchronized ObjectNode organizationWorkers(long organizationId, ObjectNode filter) {
        if (!organizations.contains(organizationId)) throw new ApiException(404, "Organization not found");
        return new WorkerSearch(filter).apply(workers.entrySet().stream()
                .filter(entry -> Long.valueOf(organizationId).equals(memberships.get(entry.getKey())))
                .map(Map.Entry::getValue).toList());
    }

    private static ObjectNode writableWorker(ObjectNode input) {
        ObjectNode writable = input.deepCopy();
        writable.remove(List.of("id", "creationDate"));
        return Validation.worker(writable);
    }

    synchronized List<ObjectNode> snapshot() {
        List<ObjectNode> result = new ArrayList<>();
        workers.values().forEach(w -> result.add(w.deepCopy()));
        return result;
    }

    public synchronized ObjectNode search(ObjectNode input) {
        return new WorkerSearch(input).apply(new ArrayList<>(workers.values()));
    }

    public synchronized BigDecimal average() {
        if (workers.isEmpty()) return null;
        BigDecimal sum = workers.values().stream().map(w -> BigDecimal.valueOf(w.get("salary").longValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(workers.size()), 10, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public synchronized ObjectNode maxPosition() {
        ObjectNode best = null;
        for (ObjectNode worker : workers.values()) {
            if (worker.path("position").isNull()) continue;
            int rank = Validation.POSITIONS.indexOf(worker.get("position").textValue());
            if (best == null || rank > Validation.POSITIONS.indexOf(best.get("position").textValue())
                    || (rank == Validation.POSITIONS.indexOf(best.get("position").textValue())
                    && worker.get("id").longValue() < best.get("id").longValue())) best = worker;
        }
        if (best == null) throw new ApiException(404, "No worker with a position");
        return best.deepCopy();
    }

    public synchronized long countBelow(int salary) {
        return workers.values().stream().filter(w -> w.get("salary").intValue() < salary).count();
    }

    public synchronized ObjectNode index(long orgId, String coefficient) {
        Validation.require(orgId > 0, 400, "orgId must be positive");
        BigDecimal coeff = coefficient(coefficient);
        if (!organizations.contains(orgId)) throw new ApiException(404, "Organization not found");
        Map<Long, ObjectNode> next = new LinkedHashMap<>(workers);
        long count = 0;
        for (var entry : workers.entrySet()) {
            ObjectNode worker = entry.getValue();
            if (!Long.valueOf(orgId).equals(memberships.get(entry.getKey()))) continue;
            BigDecimal salary = BigDecimal.valueOf(worker.get("salary").longValue()).multiply(coeff).setScale(0, RoundingMode.HALF_UP);
            Validation.require(salary.signum() > 0 && salary.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) <= 0,
                    422, "Indexed salary must be between 1 and 2147483647");
            next.put(entry.getKey(), worker.deepCopy().put("salary", salary.intValueExact()));
            count++;
        }
        if (count > 0) commit(next, new HashSet<>(organizations), new LinkedHashMap<>(memberships), lastId);
        return Json.MAPPER.createObjectNode().put("orgId", orgId).put("coeff", coeff).put("updatedCount", count);
    }

    private static BigDecimal coefficient(String value) {
        try {
            Validation.require(value != null && value.length() <= 128, 400, "Invalid coefficient");
            BigDecimal coeff = new BigDecimal(value);
            // Limit coefficient size before calculation.
            Validation.require(coeff.signum() > 0 && Math.abs((long) coeff.scale()) <= 1000 && coeff.precision() <= 128,
                    400, "Invalid coefficient");
            return coeff;
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid coefficient");
        }
    }
}
