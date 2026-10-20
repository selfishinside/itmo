package ru.itmo.soa.hr;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class HrController {
    private final WorkersClient workers;

    public HrController(WorkersClient workers) {
        this.workers = workers;
    }

    @PostMapping("/index/{worker-id}/{coeff}")
    public ResponseEntity<JsonNode> indexWorker(@PathVariable("worker-id") String id,
                                               @PathVariable("coeff") String coefficient) {
        return response(workers.indexWorker(Indexation.id(id), Indexation.coefficient(coefficient)));
    }

    @PostMapping("/index/all/{org-id}/{coeff}")
    public ResponseEntity<JsonNode> indexOrganization(@PathVariable("org-id") String id,
                                                     @PathVariable("coeff") String coefficient) {
        return response(workers.indexOrganization(Indexation.id(id), Indexation.coefficient(coefficient)));
    }

    private ResponseEntity<JsonNode> response(WorkersClient.Result result) {
        return ResponseEntity.status(result.status()).body(result.body());
    }
}
