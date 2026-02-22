package com.gully.ggos.api.admin.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRolesRequest {
    private List<UUID> roleIds;
}
