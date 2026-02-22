package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.Role;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    List<Role> findByOrg_Id(UUID orgId);
}
