package ru.itmo.love.bpm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/** шлюз к standalone camunda rest */
@Service
@Transactional
public class WorkflowGateway {
    private static final String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String CAMUNDA_NS = "http://camunda.org/schema/1.0/bpmn";
    private static final String LAB4_NS = "https://itmo.ru/blps/lab4";
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST =
            new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<Map<String, Object>> MAP =
            new ParameterizedTypeReference<>() { };

    private final RestClient camunda;
    private final long resultTimeout;

    public WorkflowGateway(@Value("${app.camunda.base-url}") String baseUrl,
                           @Value("${app.camunda.result-timeout}") long resultTimeout) {
        this.camunda = RestClient.builder().baseUrl(baseUrl).build();
        this.resultTimeout = resultTimeout;
    }

    public List<String> groups() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        return authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList();
    }

    public List<Map<String, Object>> definitions() {
        List<Map<String, Object>> body = camunda.get()
                .uri(uri -> uri.path("/process-definition")
                        .queryParam("latestVersion", true)
                        .queryParam("sortBy", "name")
                        .queryParam("sortOrder", "asc")
                        .build())
                .retrieve()
                .body(LIST);
        return (body == null ? List.<Map<String, Object>>of() : body).stream()
                .filter(definition -> LAB4_NS.equals(definition.get("category")))
                .toList();
    }

    public Map<String, Object> definition(String key) {
        Map<String, Object> definition = getMap("/process-definition/key/" + key);
        if (!LAB4_NS.equals(definition.get("category"))) {
            throw new IllegalArgumentException("Unknown lab4 process: " + key);
        }
        String allowed = processAttribute(key, "candidateStarterGroups");
        if (allowed != null && Arrays.stream(allowed.split(",")).noneMatch(groups()::contains)) {
            throw new AccessDeniedException("Role cannot start this process");
        }
        return definition;
    }

    public WorkflowInstance start(String key, Map<String, Object> input) {
        definition(key);
        Set<String> formVariables = formVariables("/process-definition/key/" + key + "/form-variables");
        if (!formVariables.isEmpty()) {
            checkFields(input, formVariables);
        }
        String endpoint = formVariables.isEmpty()
                ? "/process-definition/key/{key}/start"
                : "/process-definition/key/{key}/submit-form";
        Map<String, Object> response = camunda.post()
                .uri(endpoint, key)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("variables", variables(input)))
                .retrieve()
                .body(MAP);
        return new WorkflowInstance(Objects.toString(response.get("id")));
    }

    public Object startAndWait(String key, Map<String, Object> input, String variable) {
        WorkflowInstance instance = start(key, input);
        return variable == null ? null : waitVariable(instance.id(), variable);
    }

    public String renderedStartForm(String key) {
        definition(key);
        return camunda.get().uri("/process-definition/key/{key}/rendered-form", key)
                .retrieve().body(String.class);
    }

    public List<Map<String, Object>> tasks() {
        List<Map<String, Object>> body = camunda.get()
                .uri(uri -> uri.path("/task")
                        .queryParam("candidateGroupIn", String.join(",", groups()))
                        .build())
                .retrieve()
                .body(LIST);
        return body == null ? List.of() : body;
    }

    public Map<String, Object> task(String id) {
        Map<String, Object> task = getMap("/task/" + id);
        List<Map<String, Object>> links = camunda.get()
                .uri("/task/{id}/identity-links", id)
                .retrieve()
                .body(LIST);
        if (links == null) links = List.of();
        boolean allowed = links.stream().anyMatch(link ->
                "candidate".equals(link.get("type")) && groups().contains(Objects.toString(link.get("groupId"))));
        if (!allowed) throw new AccessDeniedException("Role cannot complete this task");
        return task;
    }

    public String renderedTaskForm(String id) {
        task(id);
        return camunda.get().uri("/task/{id}/rendered-form", id).retrieve().body(String.class);
    }

    public void complete(String id, Map<String, Object> input) {
        task(id);
        checkFields(input, formVariables("/task/" + id + "/form-variables"));
        camunda.post()
                .uri("/task/{id}/submit-form", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("variables", variables(input)))
                .retrieve()
                .toBodilessEntity();
    }

    public void completeBooking(Long bookingId, String activity, Map<String, Object> input) {
        List<Map<String, Object>> tasks = camunda.get()
                .uri(uri -> uri.path("/task")
                        .queryParam("processVariables", "bookingId_eq_" + bookingId)
                        .queryParam("taskDefinitionKey", activity)
                        .build())
                .retrieve()
                .body(LIST);
        if (tasks == null) tasks = List.of();
        if (tasks.isEmpty()) throw new IllegalStateException("Booking is not waiting for " + activity);
        complete(Objects.toString(tasks.get(0).get("id")), input);
    }

    public Object processVariable(String processInstanceId, String name) {
        try {
            Map<String, Object> value = camunda.get()
                    .uri("/process-instance/{id}/variables/{name}", processInstanceId, name)
                    .retrieve()
                    .body(MAP);
            return value == null ? null : value.get("value");
        } catch (HttpStatusCodeException ignored) {
            return historicVariable(processInstanceId, name);
        }
    }

    public Object waitVariable(String processInstanceId, String name) {
        long deadline = System.nanoTime() + Duration.ofMillis(resultTimeout).toNanos();
        Object value = processVariable(processInstanceId, name);
        while (value == null && System.nanoTime() < deadline) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting Camunda result", e);
            }
            value = processVariable(processInstanceId, name);
        }
        if (value == null) throw new IllegalStateException("Camunda process did not produce " + name);
        return value;
    }

    public void deploy(String resourceName, byte[] xml) {
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("deployment-name", "camunda-modeler-upload");
        body.add("enable-duplicate-filtering", "true");
        body.add("data", new ByteArrayResource(xml) {
            @Override
            public String getFilename() {
                return resourceName;
            }
        });
        camunda.post()
                .uri("/deployment/create")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private Object historicVariable(String processInstanceId, String name) {
        List<Map<String, Object>> values = camunda.get()
                .uri(uri -> uri.path("/history/variable-instance")
                        .queryParam("processInstanceId", processInstanceId)
                        .queryParam("variableName", name)
                        .queryParam("deserializeValues", false)
                        .build())
                .retrieve()
                .body(LIST);
        if (values == null) values = List.of();
        return values.isEmpty() ? null : values.get(0).get("value");
    }

    private Set<String> formVariables(String uri) {
        return getMap(uri).keySet();
    }

    private void checkFields(Map<String, Object> input, Set<String> allowed) {
        if (!allowed.containsAll(input.keySet())) {
            throw new IllegalArgumentException("Unexpected form fields");
        }
    }

    private Map<String, Object> variables(Map<String, Object> input) {
        Map<String, Object> result = new LinkedHashMap<>();
        input.forEach((key, value) -> result.put(key, Map.of("value", value)));
        return result;
    }

    private String processAttribute(String key, String name) {
        Map<String, Object> xml = getMap("/process-definition/key/" + key + "/xml");
        String source = Objects.toString(xml.get("bpmn20Xml"));
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            var processes = document.getElementsByTagNameNS(BPMN_NS, "process");
            for (int i = 0; i < processes.getLength(); i++) {
                var process = processes.item(i);
                var id = process.getAttributes().getNamedItem("id");
                if (id != null && key.equals(id.getNodeValue())) {
                    var attribute = process.getAttributes().getNamedItemNS(CAMUNDA_NS, name);
                    return attribute == null ? null : attribute.getNodeValue();
                }
            }
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read BPMN model for " + key, e);
        }
    }

    private Map<String, Object> getMap(String uri) {
        Map<String, Object> value = camunda.get().uri(uri).retrieve().body(MAP);
        if (value == null) throw new IllegalStateException("Empty Camunda response");
        return value;
    }

    public record WorkflowInstance(String id) { }
}
