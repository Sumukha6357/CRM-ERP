package com.gully.ggos.service;

import com.gully.ggos.api.crm.dto.ActivityRequest;
import com.gully.ggos.domain.entity.CrmActivity;
import com.gully.ggos.domain.entity.CrmDeal;
import com.gully.ggos.domain.entity.CrmLead;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.CrmActivityRepository;
import com.gully.ggos.domain.repository.CrmDealRepository;
import com.gully.ggos.domain.repository.CrmLeadRepository;
import com.gully.ggos.domain.repository.OrgRepository;
import com.gully.ggos.domain.repository.UserRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrmActivityService {
    private final CrmActivityRepository activityRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final CrmLeadRepository leadRepository;
    private final CrmDealRepository dealRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public CrmActivityService(
            CrmActivityRepository activityRepository,
            OrgRepository orgRepository,
            UserRepository userRepository,
            CrmLeadRepository leadRepository,
            CrmDealRepository dealRepository,
            AuditService auditService,
            NotificationService notificationService,
            CurrentUserService currentUserService) {
        this.activityRepository = activityRepository;
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    public Page<CrmActivity> list(UUID orgId, UUID leadId, UUID dealId, UUID ownerId, Pageable pageable) {
        return activityRepository.findFiltered(orgId, leadId, dealId, ownerId, pageable);
    }

    public CrmActivity get(UUID id, UUID orgId) {
        CrmActivity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        if (!activity.getOrg().getId().equals(orgId)) {
            throw new IllegalArgumentException("Not allowed");
        }
        return activity;
    }

    @Transactional
    public CrmActivity create(ActivityRequest request) {
        UUID orgId = currentUserService.getOrgId();
        Org org = orgRepository.findById(orgId).orElseThrow(() -> new IllegalArgumentException("Org not found"));
        CrmActivity activity = new CrmActivity();
        activity.setOrg(org);
        applyRequest(activity, request);
        CrmActivity saved = activityRepository.save(activity);
        auditService.logChange("CrmActivity", saved.getId(), "CREATE", null, saved);
        notifyOwner(saved, "CRM Activity Created", "A CRM activity was created");
        return saved;
    }

    @Transactional
    public CrmActivity update(UUID id, ActivityRequest request) {
        UUID orgId = currentUserService.getOrgId();
        CrmActivity activity = get(id, orgId);
        CrmActivity before = new CrmActivity();
        before.setId(activity.getId());
        before.setSubject(activity.getSubject());
        before.setActivityType(activity.getActivityType());
        applyRequest(activity, request);
        CrmActivity saved = activityRepository.save(activity);
        auditService.logChange("CrmActivity", saved.getId(), "UPDATE", before, saved);
        notifyOwner(saved, "CRM Activity Updated", "A CRM activity was updated");
        return saved;
    }

    public void delete(UUID id) {
        UUID orgId = currentUserService.getOrgId();
        CrmActivity activity = get(id, orgId);
        activityRepository.delete(activity);
        auditService.logChange("CrmActivity", activity.getId(), "DELETE", activity, null);
    }

    private void applyRequest(CrmActivity activity, ActivityRequest request) {
        activity.setActivityType(request.getActivityType());
        activity.setSubject(request.getSubject());
        activity.setNotes(request.getNotes());
        activity.setDueAt(request.getDueAt());
        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            activity.setOwner(owner);
        }
        if (request.getLeadId() != null) {
            CrmLead lead = leadRepository.findById(request.getLeadId())
                    .orElseThrow(() -> new IllegalArgumentException("Lead not found"));
            activity.setLead(lead);
        }
        if (request.getDealId() != null) {
            CrmDeal deal = dealRepository.findById(request.getDealId())
                    .orElseThrow(() -> new IllegalArgumentException("Deal not found"));
            activity.setDeal(deal);
        }
    }

    private void notifyOwner(CrmActivity activity, String title, String body) {
        if (activity.getOwner() != null) {
            notificationService.create(
                    activity.getOrg().getId(),
                    activity.getOwner().getId(),
                    title,
                    body,
                    "CRM",
                    null);
        }
    }
}
