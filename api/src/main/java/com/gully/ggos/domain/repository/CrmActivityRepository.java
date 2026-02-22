package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.CrmActivity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrmActivityRepository extends JpaRepository<CrmActivity, UUID> {
    @Query("""
        select a from CrmActivity a
        where a.org.id = :orgId
          and (:leadId is null or a.lead.id = :leadId)
          and (:dealId is null or a.deal.id = :dealId)
          and (:ownerId is null or a.owner.id = :ownerId)
        """)
    Page<CrmActivity> findFiltered(
        @Param("orgId") UUID orgId,
        @Param("leadId") UUID leadId,
        @Param("dealId") UUID dealId,
        @Param("ownerId") UUID ownerId,
        Pageable pageable
    );
}
