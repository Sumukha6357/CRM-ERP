package com.gully.ggos.api.crm.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeadRequest {
    @NotBlank
    private String name;
    private String email;
    private String phone;
    private String company;
    private BigDecimal value;
    private String status;
    private String source;
    private UUID stageId;
    private UUID ownerId;
}
