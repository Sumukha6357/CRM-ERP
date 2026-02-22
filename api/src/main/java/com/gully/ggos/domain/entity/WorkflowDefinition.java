package com.gully.ggos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "workflow_defs")
public class WorkflowDefinition extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Column(nullable = false, length = 128)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private String config;

    @Column(nullable = false)
    private boolean active = true;
}
