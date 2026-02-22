package com.gully.ggos.api.notifications.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String body;
    private String type;
    private String data;
    private OffsetDateTime readAt;
    private OffsetDateTime createdAt;
}
