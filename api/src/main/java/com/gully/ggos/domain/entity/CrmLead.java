package com.gully.ggos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "crm_leads")
public class CrmLead extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    private CrmPipelineStage stage;

    @Column(nullable = false, length = 64)
    private String status = "OPEN";

    @Column(length = 128)
    private String source;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String email;

    @Column(length = 64)
    private String phone;

    @Column(length = 255)
    private String company;

    @Column
    private BigDecimal value;
}
