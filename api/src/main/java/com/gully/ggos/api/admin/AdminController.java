package com.gully.ggos.api.admin;

import com.gully.ggos.api.admin.dto.PermissionResponse;
import com.gully.ggos.api.admin.dto.RolePermissionsRequest;
import com.gully.ggos.api.admin.dto.RoleRequest;
import com.gully.ggos.api.admin.dto.RoleResponse;
import com.gully.ggos.api.admin.dto.UserResponse;
import com.gully.ggos.api.admin.dto.UserRolesRequest;
import com.gully.ggos.api.admin.mapper.AdminMapper;
import com.gully.ggos.api.common.ApiResponse;
import com.gully.ggos.domain.entity.Role;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('SYS_ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final AdminMapper adminMapper;

    public AdminController(AdminService adminService, AdminMapper adminMapper) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
    }

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> listPermissions() {
        List<PermissionResponse> data = adminService.listPermissions().stream()
                .map(adminMapper::toPermissionResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> listRoles() {
        List<RoleResponse> data = adminService.listRoles().stream()
                .map(adminMapper::toRoleResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        Role role = adminService.createRole(request.getCode(), request.getName());
        return ResponseEntity.ok(ApiResponse.success(adminMapper.toRoleResponse(role)));
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable UUID id,
            @Valid @RequestBody RoleRequest request) {
        Role role = adminService.updateRole(id, request.getCode(), request.getName());
        return ResponseEntity.ok(ApiResponse.success(adminMapper.toRoleResponse(role)));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        adminService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/roles/{id}/permissions")
    public ResponseEntity<ApiResponse<List<String>>> getRolePermissions(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getRolePermissions(id)));
    }

    @PutMapping("/roles/{id}/permissions")
    public ResponseEntity<ApiResponse<List<String>>> setRolePermissions(
            @PathVariable UUID id,
            @RequestBody RolePermissionsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.setRolePermissions(id, request.getPermissions())));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        Page<User> page = adminService.listUsers(q, pageable);
        List<UserResponse> data = page.getContent().stream()
                .map(adminMapper::toUserResponse)
                .toList();

        ApiResponse.ApiMeta meta = ApiResponse.ApiMeta.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .build();

        return ResponseEntity.ok(ApiResponse.success(data, meta));
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<List<UUID>>> getUserRoles(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUserRoles(userId)));
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<List<UUID>>> setUserRoles(
            @PathVariable UUID userId,
            @RequestBody UserRolesRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.setUserRoles(userId, request.getRoleIds())));
    }
}
