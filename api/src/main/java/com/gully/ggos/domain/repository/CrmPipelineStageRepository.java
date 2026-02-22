package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.CrmPipelineStage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrmPipelineStageRepository extends JpaRepository<CrmPipelineStage, UUID> {
    List<CrmPipelineStage> findByOrg_Id(UUID orgId);
}
