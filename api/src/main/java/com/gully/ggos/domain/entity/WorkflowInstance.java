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
@Table(name = "workflow_instances")
public class WorkflowInstance extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private WorkflowDefinition definition;

    @Column(nullable = false, length = 32)
    private String status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "started_by", nullable = false)
    private User startedBy;

    @Column(name = "current_step", length = 128)
    private String currentStep;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    private String data;
}
