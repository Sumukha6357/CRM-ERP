package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.CrmLead;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrmLeadRepository extends JpaRepository<CrmLead, UUID> {
    @Query("""
        select l from CrmLead l
        where l.org.id = :orgId
          and (:status is null or l.status = :status)
          and (:stageId is null or l.stage.id = :stageId)
          and (:ownerId is null or l.owner.id = :ownerId)
        """)
    Page<CrmLead> findFiltered(
        @Param("orgId") UUID orgId,
        @Param("status") String status,
        @Param("stageId") UUID stageId,
        @Param("ownerId") UUID ownerId,
        Pageable pageable
    );

    @Query("select count(l) from CrmLead l where l.org.id = :orgId and l.createdAt >= :since")
    long countCreatedSince(@Param("orgId") UUID orgId, @Param("since") java.time.OffsetDateTime since);
}
