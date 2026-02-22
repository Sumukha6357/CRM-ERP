package com.gully.ggos.api.crm;

import com.gully.ggos.api.common.ApiResponse;
import com.gully.ggos.api.crm.dto.LeadRequest;
import com.gully.ggos.api.crm.dto.LeadResponse;
import com.gully.ggos.api.crm.mapper.CrmMapper;
import com.gully.ggos.domain.entity.CrmLead;
import com.gully.ggos.service.CrmLeadService;
import com.gully.ggos.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/leads")
public class CrmLeadController {
    private final CrmLeadService leadService;
    private final CurrentUserService currentUserService;
    private final CrmMapper crmMapper;

    public CrmLeadController(CrmLeadService leadService, CurrentUserService currentUserService, CrmMapper crmMapper) {
        this.leadService = leadService;
        this.currentUserService = currentUserService;
        this.crmMapper = crmMapper;
    }

    @PreAuthorize("hasPermission(null, 'CRM_LEAD_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadResponse>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID stage,
            @RequestParam(required = false) UUID owner,
            Pageable pageable) {
        Page<CrmLead> page = leadService.list(currentUserService.getOrgId(), status, stage, owner, pageable);
        List<LeadResponse> data = page.getContent().stream()
                .map(crmMapper::toLeadResponse)
                .toList();

        ApiResponse.ApiMeta meta = ApiResponse.ApiMeta.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .build();

        return ResponseEntity.ok(ApiResponse.success(data, meta));
    }

    @PreAuthorize("hasPermission(null, 'CRM_LEAD_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> get(@PathVariable UUID id) {
        CrmLead lead = leadService.get(id, currentUserService.getOrgId());
        return ResponseEntity.ok(ApiResponse.success(crmMapper.toLeadResponse(lead)));
    }

    @PreAuthorize("hasPermission(null, 'CRM_LEAD_WRITE')")
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> create(@Valid @RequestBody LeadRequest request) {
        CrmLead lead = leadService.create(request);
        return ResponseEntity.ok(ApiResponse.success(crmMapper.toLeadResponse(lead)));
    }

    @PreAuthorize("hasPermission(null, 'CRM_LEAD_WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody LeadRequest request) {
        CrmLead lead = leadService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(crmMapper.toLeadResponse(lead)));
    }

    @PreAuthorize("hasPermission(null, 'CRM_LEAD_WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        leadService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
