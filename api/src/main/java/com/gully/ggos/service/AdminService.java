package com.gully.ggos.service;

import com.gully.ggos.domain.entity.Permission;
import com.gully.ggos.domain.entity.Role;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.PermissionRepository;
import com.gully.ggos.domain.repository.RoleRepository;
import com.gully.ggos.domain.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public AdminService(
        RoleRepository roleRepository,
        PermissionRepository permissionRepository,
        UserRepository userRepository,
        CurrentUserService currentUserService,
        AuditService auditService
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    public List<Permission> listPermissions() {
        return permissionRepository.findAll();
    }

    public List<Role> listRoles() {
        return roleRepository.findByOrg_Id(currentUserService.getOrgId());
    }

    @Transactional
    public Role createRole(String code, String name) {
        Role role = new Role();
        role.setOrg(userRepository.findById(currentUserService.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"))
            .getOrg());
        role.setCode(code);
        role.setName(name);
        Role saved = roleRepository.save(role);
        auditService.logChange("Role", saved.getId(), "CREATE", null, saved);
        return saved;
    }

    @Transactional
    public Role updateRole(UUID id, String code, String name) {
        Role role = getRoleOrThrow(id);
        if ("ADMIN".equalsIgnoreCase(role.getCode())) {
            throw new IllegalArgumentException("Cannot modify ADMIN role");
        }
        role.setCode(code);
        role.setName(name);
        Role saved = roleRepository.save(role);
        auditService.logChange("Role", saved.getId(), "UPDATE", null, saved);
        return saved;
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = getRoleOrThrow(id);
        if ("ADMIN".equalsIgnoreCase(role.getCode())) {
            throw new IllegalArgumentException("Cannot delete ADMIN role");
        }
        roleRepository.delete(role);
        auditService.logChange("Role", role.getId(), "DELETE", role, null);
    }

    public List<String> getRolePermissions(UUID roleId) {
        Role role = getRoleOrThrow(roleId);
        return role.getPermissions().stream().map(Permission::getCode).toList();
    }

    @Transactional
    public List<String> setRolePermissions(UUID roleId, List<String> codes) {
        Role role = getRoleOrThrow(roleId);
        List<Permission> permissions = codes == null ? List.of() : permissionRepository.findByCodeIn(codes);
        role.setPermissions(new HashSet<>(permissions));
        Role saved = roleRepository.save(role);
        auditService.logChange("RolePermissions", saved.getId(), "UPDATE", null, saved.getPermissions());
        return saved.getPermissions().stream().map(Permission::getCode).toList();
    }

    public Page<User> listUsers(String q, Pageable pageable) {
        return userRepository.searchByOrg(currentUserService.getOrgId(), q, pageable);
    }

    public List<UUID> getUserRoles(UUID userId) {
        User user = getUserOrThrow(userId);
        return user.getRoles().stream().map(Role::getId).toList();
    }

    @Transactional
    public List<UUID> setUserRoles(UUID userId, List<UUID> roleIds) {
        User user = getUserOrThrow(userId);
        List<UUID> ids = roleIds == null ? List.of() : roleIds;
        List<Role> roles = roleRepository.findAllById(ids).stream()
            .filter(role -> role.getOrg().getId().equals(currentUserService.getOrgId()))
            .toList();
        user.setRoles(new HashSet<>(roles));
        User saved = userRepository.save(user);
        auditService.logChange("UserRoles", saved.getId(), "UPDATE", null, saved.getRoles());
        return saved.getRoles().stream().map(Role::getId).toList();
    }

    private Role getRoleOrThrow(UUID roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        if (!role.getOrg().getId().equals(currentUserService.getOrgId())) {
            throw new IllegalArgumentException("Not allowed");
        }
        return role;
    }

    private User getUserOrThrow(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getOrg().getId().equals(currentUserService.getOrgId())) {
            throw new IllegalArgumentException("Not allowed");
        }
        return user;
    }
}
