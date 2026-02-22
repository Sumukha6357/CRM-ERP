package com.gully.ggos.api.kpi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KpiResponse {
    private UUID id;
    private String metricCode;
    private BigDecimal metricValue;
    private LocalDate snapshotDate;
}
