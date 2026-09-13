package ru.itmo.love.bpm;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowGateway gateway;

    /** загружает новую версию процесса в standalone camunda */
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping(value="/deployments", consumes={"application/xml","text/xml"})
    public Map<String,String> deploy(@RequestBody byte[] xml) {
        gateway.deploy("process.bpmn", xml);
        return Map.of("status", "deployed");
    }

    @GetMapping("/processes")
    public List<Map<String,String>> processes() {
        List<Map<String,String>> result = new ArrayList<>();
        for (var d : gateway.definitions()) {
            try {
                String key = Objects.toString(d.get("key"));
                gateway.definition(key);
                result.add(Map.of("key", key, "name", Objects.toString(d.get("name"), key)));
            }
            catch (AccessDeniedException ignored) { }
        }
        return result;
    }
    @GetMapping(value="/processes/{key}/form",produces=MediaType.TEXT_HTML_VALUE)
    public String startForm(@PathVariable String key) {
        return gateway.renderedStartForm(key);
    }
    @PostMapping("/processes/{key}")
    public Map<String,Object> start(@PathVariable String key,@RequestBody Map<String,Object> input) {
        var instance = gateway.start(key,input);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("processInstanceId",instance.id());
        for (String variable : Set.of("bookingId", "resultId", "count")) {
            Object value = gateway.processVariable(instance.id(), variable);
            if (value != null) result.put(variable, value);
        }
        return result;
    }
    @GetMapping("/tasks")
    public List<Map<String,Object>> tasks() {
        List<Map<String,Object>> result=new ArrayList<>();
        for(var t: gateway.tasks()) {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("id", t.get("id"));
            row.put("name", t.get("name"));
            Object instanceId = t.get("processInstanceId");
            if (instanceId != null) row.put("bookingId", gateway.processVariable(Objects.toString(instanceId), "bookingId"));
            result.add(row);
        }
        return result;
    }
    @GetMapping(value="/tasks/{id}/form",produces=MediaType.TEXT_HTML_VALUE)
    public String taskForm(@PathVariable String id) {
        return gateway.renderedTaskForm(id);
    }
    @PostMapping("/tasks/{id}")
    public Map<String,String> complete(@PathVariable String id,@RequestBody Map<String,Object> input) {
        gateway.complete(id,input);return Map.of("status","completed");
    }
    @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class,org.springframework.web.client.RestClientException.class})
    public ResponseEntity<Map<String,String>> invalid(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error",Objects.toString(e.getMessage(),"Operation failed")));
    }
}
