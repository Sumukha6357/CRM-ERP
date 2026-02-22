package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.Org;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgRepository extends JpaRepository<Org, UUID> {
    Optional<Org> findByCode(String code);
}
