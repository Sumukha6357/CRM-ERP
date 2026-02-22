package com.gully.ggos.security;

import com.gully.ggos.service.PermissionService;
import java.io.Serializable;
import java.util.UUID;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {
    private final PermissionService permissionService;

    public PermissionEvaluatorImpl(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return hasPermission(authentication, null, null, permission);
    }

    @Override
    public boolean hasPermission(
        Authentication authentication,
        Serializable targetId,
        String targetType,
        Object permission
    ) {
        if (authentication == null || permission == null) {
            return false;
        }
        if (authentication.getPrincipal() instanceof SecurityUser securityUser) {
            UUID userId = securityUser.getUserId();
            return permissionService.userHasPermission(userId, permission.toString());
        }
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(permission.toString()));
    }
}
