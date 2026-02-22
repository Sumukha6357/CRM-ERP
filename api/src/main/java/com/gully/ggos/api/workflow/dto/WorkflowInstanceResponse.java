package com.gully.ggos.api.workflow.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkflowInstanceResponse {
    private UUID id;
    private String definitionCode;
    private String status;
    private String currentStep;
    private String data;
    private OffsetDateTime createdAt;
}
