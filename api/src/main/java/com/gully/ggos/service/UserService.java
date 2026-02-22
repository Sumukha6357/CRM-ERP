package com.gully.ggos.service;

import com.gully.ggos.domain.entity.Permission;
import com.gully.ggos.domain.entity.Role;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.UserRepository;
import com.gully.ggos.security.SecurityUser;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<SecurityUser> loadSecurityUserByEmail(String email) {
        return userRepository.findFirstByEmailIgnoreCase(email)
            .map(this::toSecurityUser);
    }

    public SecurityUser loadSecurityUserById(UUID userId) {
        return userRepository.findById(userId)
            .map(this::toSecurityUser)
            .orElse(null);
    }

    public Optional<User> findByEmailAndOrg(String email, UUID orgId) {
        return userRepository.findByEmailAndOrg_Id(email, orgId);
    }

    public SecurityUser toSecurityUser(User user) {
        Set<String> permissions = getPermissions(user);
        Set<String> roleCodes = user.getRoles().stream()
            .map(Role::getCode)
            .collect(Collectors.toSet());

        Collection<GrantedAuthority> authorities = new HashSet<>();
        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        roleCodes.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));

        boolean active = "ACTIVE".equalsIgnoreCase(user.getStatus());
        return new SecurityUser(user.getId(), user.getOrg().getId(), user.getEmail(), user.getPasswordHash(), active, authorities);
    }

    public Set<String> getPermissions(User user) {
        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getCode)
            .collect(Collectors.toSet());
    }

    public User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
