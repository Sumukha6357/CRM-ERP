package com.gully.ggos.api.admin.mapper;

import com.gully.ggos.api.admin.dto.PermissionResponse;
import com.gully.ggos.api.admin.dto.RoleResponse;
import com.gully.ggos.api.admin.dto.UserResponse;
import com.gully.ggos.domain.entity.Permission;
import com.gully.ggos.domain.entity.Role;
import com.gully.ggos.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    PermissionResponse toPermissionResponse(Permission permission);
    RoleResponse toRoleResponse(Role role);
    UserResponse toUserResponse(User user);
}
