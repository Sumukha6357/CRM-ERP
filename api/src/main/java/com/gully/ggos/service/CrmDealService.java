package com.gully.ggos.service;

import com.gully.ggos.api.crm.dto.DealRequest;
import com.gully.ggos.domain.entity.CrmDeal;
import com.gully.ggos.domain.entity.CrmPipelineStage;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.CrmDealRepository;
import com.gully.ggos.domain.repository.CrmPipelineStageRepository;
import com.gully.ggos.domain.repository.OrgRepository;
import com.gully.ggos.domain.repository.UserRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrmDealService {
    private final CrmDealRepository dealRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final CrmPipelineStageRepository stageRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public CrmDealService(
            CrmDealRepository dealRepository,
            OrgRepository orgRepository,
            UserRepository userRepository,
            CrmPipelineStageRepository stageRepository,
            AuditService auditService,
            NotificationService notificationService,
            CurrentUserService currentUserService) {
        this.dealRepository = dealRepository;
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.stageRepository = stageRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    public Page<CrmDeal> list(UUID orgId, String status, UUID stageId, UUID ownerId, Pageable pageable) {
        return dealRepository.findFiltered(orgId, status, stageId, ownerId, pageable);
    }

    public CrmDeal get(UUID id, UUID orgId) {
        CrmDeal deal = dealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deal not found"));
        if (!deal.getOrg().getId().equals(orgId)) {
            throw new IllegalArgumentException("Not allowed");
        }
        return deal;
    }

    @Transactional
    public CrmDeal create(DealRequest request) {
        UUID orgId = currentUserService.getOrgId();
        Org org = orgRepository.findById(orgId).orElseThrow(() -> new IllegalArgumentException("Org not found"));
        CrmDeal deal = new CrmDeal();
        deal.setOrg(org);
        applyRequest(deal, request);
        CrmDeal saved = dealRepository.save(deal);
        auditService.logChange("CrmDeal", saved.getId(), "CREATE", null, saved);
        notifyOwner(saved, "CRM Deal Created", "A deal was created");
        return saved;
    }

    @Transactional
    public CrmDeal update(UUID id, DealRequest request) {
        UUID orgId = currentUserService.getOrgId();
        CrmDeal deal = get(id, orgId);
        CrmDeal before = new CrmDeal();
        before.setId(deal.getId());
        before.setTitle(deal.getTitle());
        before.setStatus(deal.getStatus());
        applyRequest(deal, request);
        CrmDeal saved = dealRepository.save(deal);
        auditService.logChange("CrmDeal", saved.getId(), "UPDATE", before, saved);
        notifyOwner(saved, "CRM Deal Updated", "A deal was updated");
        return saved;
    }

    public void delete(UUID id) {
        UUID orgId = currentUserService.getOrgId();
        CrmDeal deal = get(id, orgId);
        dealRepository.delete(deal);
        auditService.logChange("CrmDeal", deal.getId(), "DELETE", deal, null);
    }

    private void applyRequest(CrmDeal deal, DealRequest request) {
        deal.setTitle(request.getTitle());
        deal.setAmount(request.getAmount());
        deal.setExpectedCloseDate(request.getExpectedCloseDate());
        if (request.getStatus() != null) {
            deal.setStatus(request.getStatus());
        }
        if (request.getStageId() != null) {
            CrmPipelineStage stage = stageRepository.findById(request.getStageId())
                    .orElseThrow(() -> new IllegalArgumentException("Stage not found"));
            deal.setStage(stage);
        }
        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            deal.setOwner(owner);
        }
    }

    private void notifyOwner(CrmDeal deal, String title, String body) {
        if (deal.getOwner() != null) {
            notificationService.create(
                    deal.getOrg().getId(),
                    deal.getOwner().getId(),
                    title,
                    body,
                    "CRM",
                    null);
        }
    }
}
