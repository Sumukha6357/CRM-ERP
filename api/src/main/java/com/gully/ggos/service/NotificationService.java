package com.gully.ggos.service;

import com.gully.ggos.domain.entity.Notification;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.NotificationRepository;
import com.gully.ggos.domain.repository.OrgRepository;
import com.gully.ggos.domain.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;

    public NotificationService(
        NotificationRepository notificationRepository,
        OrgRepository orgRepository,
        UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
    }

    public Notification create(UUID orgId, UUID userId, String title, String body, String type, String data) {
        Org org = orgRepository.findById(orgId).orElseThrow(() -> new IllegalArgumentException("Org not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Notification notification = new Notification();
        notification.setOrg(org);
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setType(type);
        notification.setData(data);
        return notificationRepository.save(notification);
    }

    public Page<Notification> list(UUID orgId, UUID userId, Pageable pageable) {
        return notificationRepository.findByOrg_IdAndUser_Id(orgId, userId, pageable);
    }

    public Notification markRead(UUID id, UUID orgId, UUID userId) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getOrg().getId().equals(orgId) || !notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not allowed");
        }
        notification.setReadAt(OffsetDateTime.now());
        return notificationRepository.save(notification);
    }
}
