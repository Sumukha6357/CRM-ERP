package com.gully.ggos.api.crm.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealRequest {
    @NotBlank
    private String title;
    private BigDecimal amount;
    private LocalDate expectedCloseDate;
    private String status;
    private UUID stageId;
    private UUID ownerId;
}
