package com.gully.ggos.api.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DealResponse {
    private UUID id;
    private String title;
    private BigDecimal amount;
    private LocalDate expectedCloseDate;
    private String status;
    private UUID stageId;
    private UUID ownerId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
