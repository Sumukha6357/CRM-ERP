package com.gully.ggos.service;

import com.gully.ggos.api.crm.dto.LeadRequest;
import com.gully.ggos.domain.entity.CrmLead;
import com.gully.ggos.domain.entity.CrmPipelineStage;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.CrmLeadRepository;
import com.gully.ggos.domain.repository.CrmPipelineStageRepository;
import com.gully.ggos.domain.repository.OrgRepository;
import com.gully.ggos.domain.repository.UserRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrmLeadService {
    private final CrmLeadRepository leadRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final CrmPipelineStageRepository stageRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public CrmLeadService(
            CrmLeadRepository leadRepository,
            OrgRepository orgRepository,
            UserRepository userRepository,
            CrmPipelineStageRepository stageRepository,
            AuditService auditService,
            NotificationService notificationService,
            CurrentUserService currentUserService) {
        this.leadRepository = leadRepository;
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.stageRepository = stageRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    public Page<CrmLead> list(UUID orgId, String status, UUID stageId, UUID ownerId, Pageable pageable) {
        return leadRepository.findFiltered(orgId, status, stageId, ownerId, pageable);
    }

    public CrmLead get(UUID id, UUID orgId) {
        CrmLead lead = leadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found"));
        if (!lead.getOrg().getId().equals(orgId)) {
            throw new IllegalArgumentException("Not allowed");
        }
        return lead;
    }

    @Transactional
    public CrmLead create(LeadRequest request) {
        UUID orgId = currentUserService.getOrgId();
        Org org = orgRepository.findById(orgId).orElseThrow(() -> new IllegalArgumentException("Org not found"));
        CrmLead lead = new CrmLead();
        lead.setOrg(org);
        applyRequest(lead, request);
        CrmLead saved = leadRepository.save(lead);
        auditService.logChange("CrmLead", saved.getId(), "CREATE", null, saved);
        notifyOwner(saved, "CRM Lead Created", "A lead was created");
        return saved;
    }

    @Transactional
    public CrmLead update(UUID id, LeadRequest request) {
        UUID orgId = currentUserService.getOrgId();
        CrmLead lead = get(id, orgId);
        CrmLead before = new CrmLead();
        before.setId(lead.getId());
        before.setName(lead.getName());
        before.setStatus(lead.getStatus());
        applyRequest(lead, request);
        CrmLead saved = leadRepository.save(lead);
        auditService.logChange("CrmLead", saved.getId(), "UPDATE", before, saved);
        notifyOwner(saved, "CRM Lead Updated", "A lead was updated");
        return saved;
    }

    public void delete(UUID id) {
        UUID orgId = currentUserService.getOrgId();
        CrmLead lead = get(id, orgId);
        leadRepository.delete(lead);
        auditService.logChange("CrmLead", lead.getId(), "DELETE", lead, null);
    }

    private void applyRequest(CrmLead lead, LeadRequest request) {
        lead.setName(request.getName());
        lead.setEmail(request.getEmail());
        lead.setPhone(request.getPhone());
        lead.setCompany(request.getCompany());
        lead.setValue(request.getValue());
        if (request.getStatus() != null) {
            lead.setStatus(request.getStatus());
        }
        lead.setSource(request.getSource());
        if (request.getStageId() != null) {
            CrmPipelineStage stage = stageRepository.findById(request.getStageId())
                    .orElseThrow(() -> new IllegalArgumentException("Stage not found"));
            lead.setStage(stage);
        }
        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            lead.setOwner(owner);
        }
    }

    private void notifyOwner(CrmLead lead, String title, String body) {
        if (lead.getOwner() != null) {
            notificationService.create(
                    lead.getOrg().getId(),
                    lead.getOwner().getId(),
                    title,
                    body,
                    "CRM",
                    null);
        }
    }
}
