package com.gully.ggos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "crm_activities")
public class CrmActivity extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private CrmLead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id")
    private CrmDeal deal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "activity_type", nullable = false, length = 64)
    private String activityType;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column
    private String notes;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;
}
