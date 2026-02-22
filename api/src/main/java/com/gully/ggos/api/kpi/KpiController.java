package com.gully.ggos.api.kpi;

import com.gully.ggos.api.kpi.dto.KpiResponse;
import com.gully.ggos.domain.entity.KpiSnapshot;
import com.gully.ggos.service.CurrentUserService;
import com.gully.ggos.service.KpiService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {
    private final KpiService kpiService;
    private final CurrentUserService currentUserService;

    public KpiController(KpiService kpiService, CurrentUserService currentUserService) {
        this.kpiService = kpiService;
        this.currentUserService = currentUserService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<KpiResponse>> list() {
        List<KpiSnapshot> snapshots = kpiService.getLatestByOrg(currentUserService.getOrgId());
        List<KpiResponse> response = snapshots.stream()
            .map(snapshot -> new KpiResponse(
                snapshot.getId(),
                snapshot.getMetricCode(),
                snapshot.getMetricValue(),
                snapshot.getSnapshotDate()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
