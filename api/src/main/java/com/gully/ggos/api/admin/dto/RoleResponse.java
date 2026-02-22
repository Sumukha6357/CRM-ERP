package com.gully.ggos.api.admin.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleResponse {
    private UUID id;
    private String code;
    private String name;
}
