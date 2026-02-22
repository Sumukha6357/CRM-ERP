package com.gully.ggos.service;

import com.gully.ggos.api.workflow.dto.StartWorkflowRequest;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.entity.WorkflowDefinition;
import com.gully.ggos.domain.entity.WorkflowInstance;
import com.gully.ggos.domain.repository.OrgRepository;
import com.gully.ggos.domain.repository.UserRepository;
import com.gully.ggos.domain.repository.WorkflowDefinitionRepository;
import com.gully.ggos.domain.repository.WorkflowInstanceRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowService {
    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public WorkflowService(
            WorkflowDefinitionRepository definitionRepository,
            WorkflowInstanceRepository instanceRepository,
            OrgRepository orgRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService,
            NotificationService notificationService) {
        this.definitionRepository = definitionRepository;
        this.instanceRepository = instanceRepository;
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
    }

    @Transactional
    public WorkflowInstance start(String code, StartWorkflowRequest request) {
        UUID orgId = currentUserService.getOrgId();
        WorkflowDefinition definition = definitionRepository.findByOrg_IdAndCode(orgId, code)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found"));
        Org org = orgRepository.findById(orgId).orElseThrow(() -> new IllegalArgumentException("Org not found"));
        User user = userRepository.findById(currentUserService.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        WorkflowInstance instance = new WorkflowInstance();
        instance.setOrg(org);
        instance.setDefinition(definition);
        instance.setStatus("PENDING");
        instance.setStartedBy(user);
        instance.setCurrentStep("start");
        instance.setData(request.getData());
        WorkflowInstance saved = instanceRepository.save(instance);
        notificationService.create(orgId, user.getId(), "Workflow Started", "Workflow started: " + code, "WORKFLOW",
                null);
        return saved;
    }

    public Page<WorkflowInstance> list(Pageable pageable) {
        return instanceRepository.findByOrg_Id(currentUserService.getOrgId(), pageable);
    }

    @Transactional
    public WorkflowInstance approve(UUID id) {
        WorkflowInstance instance = getInstance(id);
        instance.setStatus("APPROVED");
        instance.setCurrentStep("approved");
        WorkflowInstance saved = instanceRepository.save(instance);
        notificationService.create(
                saved.getOrg().getId(),
                saved.getStartedBy().getId(),
                "Workflow Approved",
                "Workflow approved: " + saved.getDefinition().getCode(),
                "WORKFLOW",
                null);
        return saved;
    }

    @Transactional
    public WorkflowInstance reject(UUID id) {
        WorkflowInstance instance = getInstance(id);
        instance.setStatus("REJECTED");
        instance.setCurrentStep("rejected");
        WorkflowInstance saved = instanceRepository.save(instance);
        notificationService.create(
                saved.getOrg().getId(),
                saved.getStartedBy().getId(),
                "Workflow Rejected",
                "Workflow rejected: " + saved.getDefinition().getCode(),
                "WORKFLOW",
                null);
        return saved;
    }

    private WorkflowInstance getInstance(UUID id) {
        WorkflowInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found"));
        if (!instance.getOrg().getId().equals(currentUserService.getOrgId())) {
            throw new IllegalArgumentException("Not allowed");
        }
        return instance;
    }
}
