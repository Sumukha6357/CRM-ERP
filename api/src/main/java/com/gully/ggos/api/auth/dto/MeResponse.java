package com.gully.ggos.api.auth.dto;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeResponse {
    private UUID id;
    private UUID orgId;
    private String email;
    private String fullName;
    private Set<String> roles;
    private Set<String> permissions;
}
