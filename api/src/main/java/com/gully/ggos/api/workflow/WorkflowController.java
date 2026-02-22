package com.gully.ggos.api.workflow;

import com.gully.ggos.domain.entity.WorkflowInstance;
import com.gully.ggos.api.workflow.dto.StartWorkflowRequest;
import com.gully.ggos.api.workflow.dto.WorkflowInstanceResponse;
import com.gully.ggos.service.WorkflowService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PreAuthorize("hasPermission(null, 'WORKFLOW_WRITE')")
    @PostMapping("/{code}/instances")
    public ResponseEntity<WorkflowInstanceResponse> start(
            @PathVariable String code,
            @RequestBody StartWorkflowRequest request) {
        WorkflowInstance instance = workflowService.start(code, request);
        return ResponseEntity.ok(toResponse(instance));
    }

    @PreAuthorize("hasPermission(null, 'WORKFLOW_READ')")
    @GetMapping("/instances")
    public ResponseEntity<Page<WorkflowInstanceResponse>> list(Pageable pageable) {
        Page<WorkflowInstanceResponse> results = workflowService.list(pageable).map(this::toResponse);
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasPermission(null, 'WORKFLOW_WRITE')")
    @PostMapping("/instances/{id}/approve")
    public ResponseEntity<WorkflowInstanceResponse> approve(@PathVariable UUID id) {
        WorkflowInstance instance = workflowService.approve(id);
        return ResponseEntity.ok(toResponse(instance));
    }

    @PreAuthorize("hasPermission(null, 'WORKFLOW_WRITE')")
    @PostMapping("/instances/{id}/reject")
    public ResponseEntity<WorkflowInstanceResponse> reject(@PathVariable UUID id) {
        WorkflowInstance instance = workflowService.reject(id);
        return ResponseEntity.ok(toResponse(instance));
    }

    private WorkflowInstanceResponse toResponse(WorkflowInstance instance) {
        return new WorkflowInstanceResponse(
                instance.getId(),
                instance.getDefinition().getCode(),
                instance.getStatus(),
                instance.getCurrentStep(),
                instance.getData(),
                instance.getCreatedAt());
    }
}
