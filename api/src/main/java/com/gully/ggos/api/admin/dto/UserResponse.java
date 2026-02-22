package com.gully.ggos.api.admin.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String status;
}
