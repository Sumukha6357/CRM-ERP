package com.gully.ggos.api.admin.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionsRequest {
    private List<String> permissions;
}
