package com.gully.ggos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kpi_snapshots")
public class KpiSnapshot extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Column(name = "metric_code", nullable = false, length = 128)
    private String metricCode;

    @Column(name = "metric_value", nullable = false)
    private BigDecimal metricValue;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;
}
