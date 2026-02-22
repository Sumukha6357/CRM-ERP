package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.WorkflowInstance;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, UUID> {
    Page<WorkflowInstance> findByOrg_Id(UUID orgId, Pageable pageable);
}
