package com.gully.ggos.api.crm;

import com.gully.ggos.domain.entity.CrmDeal;
import com.gully.ggos.api.crm.dto.DealRequest;
import com.gully.ggos.api.crm.dto.DealResponse;
import com.gully.ggos.service.CrmDealService;
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
@RequestMapping("/api/crm/deals")
public class CrmDealController {
    private final CrmDealService dealService;
    private final CurrentUserService currentUserService;

    public CrmDealController(CrmDealService dealService, CurrentUserService currentUserService) {
        this.dealService = dealService;
        this.currentUserService = currentUserService;
    }

    @PreAuthorize("hasPermission(null, 'CRM_DEAL_READ')")
    @GetMapping
    public ResponseEntity<Page<DealResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID stage,
            @RequestParam(required = false) UUID owner,
            Pageable pageable) {
        Page<DealResponse> results = dealService
                .list(currentUserService.getOrgId(), status, stage, owner, pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasPermission(null, 'CRM_DEAL_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<DealResponse> get(@PathVariable UUID id) {
        CrmDeal deal = dealService.get(id, currentUserService.getOrgId());
        return ResponseEntity.ok(toResponse(deal));
    }

    @PreAuthorize("hasPermission(null, 'CRM_DEAL_WRITE')")
    @PostMapping
    public ResponseEntity<DealResponse> create(@RequestBody DealRequest request) {
        CrmDeal deal = dealService.create(request);
        return ResponseEntity.ok(toResponse(deal));
    }

    @PreAuthorize("hasPermission(null, 'CRM_DEAL_WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<DealResponse> update(@PathVariable UUID id, @RequestBody DealRequest request) {
        CrmDeal deal = dealService.update(id, request);
        return ResponseEntity.ok(toResponse(deal));
    }

    @PreAuthorize("hasPermission(null, 'CRM_DEAL_WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dealService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private DealResponse toResponse(CrmDeal deal) {
        return new DealResponse(
                deal.getId(),
                deal.getTitle(),
                deal.getAmount(),
                deal.getExpectedCloseDate(),
                deal.getStatus(),
                deal.getStage() != null ? deal.getStage().getId() : null,
                deal.getOwner() != null ? deal.getOwner().getId() : null,
                deal.getCreatedAt(),
                deal.getUpdatedAt());
    }
}
