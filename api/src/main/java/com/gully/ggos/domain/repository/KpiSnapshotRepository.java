package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.KpiSnapshot;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiSnapshotRepository extends JpaRepository<KpiSnapshot, UUID> {
    boolean existsByOrg_IdAndMetricCodeAndSnapshotDate(UUID orgId, String metricCode, LocalDate snapshotDate);

    @org.springframework.data.jpa.repository.Query(
        value = """
            select distinct on (metric_code) *
            from kpi_snapshots
            where org_id = :orgId
            order by metric_code, snapshot_date desc, created_at desc
            """,
        nativeQuery = true
    )
    java.util.List<KpiSnapshot> findLatestByOrgId(@org.springframework.data.repository.query.Param("orgId") UUID orgId);
}
