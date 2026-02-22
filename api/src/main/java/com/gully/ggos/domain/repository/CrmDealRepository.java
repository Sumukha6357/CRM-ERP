package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.CrmDeal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrmDealRepository extends JpaRepository<CrmDeal, UUID> {
    @Query("""
        select d from CrmDeal d
        where d.org.id = :orgId
          and (:status is null or d.status = :status)
          and (:stageId is null or d.stage.id = :stageId)
          and (:ownerId is null or d.owner.id = :ownerId)
        """)
    Page<CrmDeal> findFiltered(
        @Param("orgId") UUID orgId,
        @Param("status") String status,
        @Param("stageId") UUID stageId,
        @Param("ownerId") UUID ownerId,
        Pageable pageable
    );
}
