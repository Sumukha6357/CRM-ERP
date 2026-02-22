package com.gully.ggos.service;

import com.gully.ggos.security.SecurityUser;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public UUID getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getUserId();
        }
        return null;
    }

    public UUID getOrgId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getOrgId();
        }
        return null;
    }
}
