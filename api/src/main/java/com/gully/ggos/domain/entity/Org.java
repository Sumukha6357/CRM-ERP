package com.gully.ggos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orgs")
public class Org extends BaseEntity {
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;
}
