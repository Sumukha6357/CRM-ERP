package com.gully.ggos.api.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;
}
