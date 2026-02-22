package com.gully.ggos.service;

import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    private final UserRepository userRepository;

    public PermissionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userHasPermission(UUID userId, String permissionCode) {
        return userRepository.findById(userId)
            .map(User::getRoles)
            .map(roles -> roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.getCode().equals(permissionCode)))
            .orElse(false);
    }

    public User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
