package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.Notification;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByOrg_IdAndUser_Id(UUID orgId, UUID userId, Pageable pageable);
}
