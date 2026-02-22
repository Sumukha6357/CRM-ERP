package com.gully.ggos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "audit_log")
public class AuditLog extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(name = "entity_type", nullable = false, length = 128)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(nullable = false, length = 64)
    private String action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_state")
    private String beforeState;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_state")
    private String afterState;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;
}
