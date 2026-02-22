package com.gully.ggos.api.crm.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeadResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private BigDecimal value;
    private String status;
    private String source;
    private UUID stageId;
    private UUID ownerId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
