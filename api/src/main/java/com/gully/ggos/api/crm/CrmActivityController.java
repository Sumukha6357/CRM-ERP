package com.gully.ggos.api.crm;

import com.gully.ggos.domain.entity.CrmActivity;
import com.gully.ggos.api.crm.dto.ActivityRequest;
import com.gully.ggos.api.crm.dto.ActivityResponse;
import com.gully.ggos.service.CrmActivityService;
import com.gully.ggos.service.CurrentUserService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crm/activities")
public class CrmActivityController {
    private final CrmActivityService activityService;
    private final CurrentUserService currentUserService;

    public CrmActivityController(CrmActivityService activityService, CurrentUserService currentUserService) {
        this.activityService = activityService;
        this.currentUserService = currentUserService;
    }

    @PreAuthorize("hasPermission(null, 'CRM_ACTIVITY_READ')")
    @GetMapping
    public ResponseEntity<Page<ActivityResponse>> list(
            @RequestParam(required = false) UUID lead,
            @RequestParam(required = false) UUID deal,
            @RequestParam(required = false) UUID owner,
            Pageable pageable) {
        Page<ActivityResponse> results = activityService
                .list(currentUserService.getOrgId(), lead, deal, owner, pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasPermission(null, 'CRM_ACTIVITY_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> get(@PathVariable UUID id) {
        CrmActivity activity = activityService.get(id, currentUserService.getOrgId());
        return ResponseEntity.ok(toResponse(activity));
    }

    @PreAuthorize("hasPermission(null, 'CRM_ACTIVITY_WRITE')")
    @PostMapping
    public ResponseEntity<ActivityResponse> create(@RequestBody ActivityRequest request) {
        CrmActivity activity = activityService.create(request);
        return ResponseEntity.ok(toResponse(activity));
    }

    @PreAuthorize("hasPermission(null, 'CRM_ACTIVITY_WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponse> update(@PathVariable UUID id, @RequestBody ActivityRequest request) {
        CrmActivity activity = activityService.update(id, request);
        return ResponseEntity.ok(toResponse(activity));
    }

    @PreAuthorize("hasPermission(null, 'CRM_ACTIVITY_WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ActivityResponse toResponse(CrmActivity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getActivityType(),
                activity.getSubject(),
                activity.getNotes(),
                activity.getDueAt(),
                activity.getLead() != null ? activity.getLead().getId() : null,
                activity.getDeal() != null ? activity.getDeal().getId() : null,
                activity.getOwner() != null ? activity.getOwner().getId() : null,
                activity.getCreatedAt());
    }
}
