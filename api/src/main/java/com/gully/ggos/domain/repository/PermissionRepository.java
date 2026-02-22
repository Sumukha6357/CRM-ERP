package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.Permission;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByCode(String code);
    java.util.List<Permission> findByCodeIn(java.util.Collection<String> codes);
}
