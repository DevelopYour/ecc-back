package com.seoultech.ecc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @Column(name="updated_at")
    private LocalDateTime updatedAt; // 수정일시

    @Column(name = "created_by", updatable = false)
    private String createdBy; // 생성자

    @Column(name = "updated_by")
    private String updatedBy; // 수정자

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.createdBy = "system"; // TODO: 실제 사용자 ID로 변경 필요
        this.updatedBy = "system"; // TODO: 실제 사용자 ID로 변경 필요
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = "system"; // TODO: 실제 사용자 ID로 변경 필요
    }
}
