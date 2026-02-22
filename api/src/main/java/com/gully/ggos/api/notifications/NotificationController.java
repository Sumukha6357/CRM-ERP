package com.gully.ggos.api.notifications;

import com.gully.ggos.domain.entity.Notification;
import com.gully.ggos.api.notifications.dto.NotificationResponse;
import com.gully.ggos.service.CurrentUserService;
import com.gully.ggos.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService, CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @PreAuthorize("hasPermission(null, 'NOTIFICATION_READ')")
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> list(Pageable pageable) {
        Page<NotificationResponse> results = notificationService
                .list(currentUserService.getOrgId(), currentUserService.getUserId(), pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(results);
    }

    @PreAuthorize("hasPermission(null, 'NOTIFICATION_WRITE')")
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable("id") java.util.UUID id) {
        Notification notification = notificationService.markRead(
                id,
                currentUserService.getOrgId(),
                currentUserService.getUserId());
        return ResponseEntity.ok(toResponse(notification));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getType(),
                notification.getData(),
                notification.getReadAt(),
                notification.getCreatedAt());
    }
}
