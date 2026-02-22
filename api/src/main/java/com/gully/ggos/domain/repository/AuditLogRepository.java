package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.AuditLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
