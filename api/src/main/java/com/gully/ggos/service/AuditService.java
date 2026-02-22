package com.gully.ggos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gully.ggos.domain.entity.AuditLog;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.repository.AuditLogRepository;
import com.gully.ggos.domain.repository.OrgRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final OrgRepository orgRepository;
    private final ObjectMapper objectMapper;
    private final CurrentUserService currentUserService;
    private final HttpServletRequest request;

    public AuditService(
        AuditLogRepository auditLogRepository,
        OrgRepository orgRepository,
        ObjectMapper objectMapper,
        CurrentUserService currentUserService,
        HttpServletRequest request
    ) {
        this.auditLogRepository = auditLogRepository;
        this.orgRepository = orgRepository;
        this.objectMapper = objectMapper;
        this.currentUserService = currentUserService;
        this.request = request;
    }

    public void logChange(String entityType, UUID entityId, String action, Object before, Object after) {
        UUID orgId = currentUserService.getOrgId();
        if (orgId == null) {
            return;
        }
        Org org = orgRepository.findById(orgId)
            .orElseThrow(() -> new IllegalArgumentException("Org not found"));
        AuditLog log = new AuditLog();
        log.setOrg(org);
        log.setActorUserId(currentUserService.getUserId());
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setBeforeState(toJson(before));
        log.setAfterState(toJson(after));
        log.setIpAddress(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));
        auditLogRepository.save(log);
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
