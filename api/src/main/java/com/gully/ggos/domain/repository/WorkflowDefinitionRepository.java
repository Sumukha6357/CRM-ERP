package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.WorkflowDefinition;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, UUID> {
    Optional<WorkflowDefinition> findByOrg_IdAndCode(UUID orgId, String code);
}
