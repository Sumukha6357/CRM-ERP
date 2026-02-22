package com.gully.ggos.api.crm.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActivityResponse {
    private UUID id;
    private String activityType;
    private String subject;
    private String notes;
    private OffsetDateTime dueAt;
    private UUID leadId;
    private UUID dealId;
    private UUID ownerId;
    private OffsetDateTime createdAt;
}
