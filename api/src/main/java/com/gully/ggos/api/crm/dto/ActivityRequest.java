package com.gully.ggos.api.crm.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityRequest {
    @NotBlank
    private String activityType;
    @NotBlank
    private String subject;
    private String notes;
    private OffsetDateTime dueAt;
    private UUID leadId;
    private UUID dealId;
    private UUID ownerId;
}
