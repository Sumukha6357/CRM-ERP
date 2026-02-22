package com.gully.ggos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gully.ggos.domain.entity.Permission;
import com.gully.ggos.domain.entity.Role;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.UserRepository;
import com.gully.ggos.service.PermissionService;
import com.gully.ggos.service.UserService;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @InjectMocks
    PermissionService permissionService;

    @Test
    void userHasPermission() {
        UUID userId = UUID.randomUUID();
        Permission perm = new Permission();
        perm.setCode("CRM_LEAD_READ");
        Role role = new Role();
        role.setPermissions(Set.of(perm));
        User user = new User();
        user.setRoles(Set.of(role));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThat(permissionService.userHasPermission(userId, "CRM_LEAD_READ")).isTrue();
        assertThat(permissionService.userHasPermission(userId, "CRM_LEAD_WRITE")).isFalse();
    }
}
