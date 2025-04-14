package com.seoultech.ecc.global;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @Column(name="updated_at")
    private LocalDateTime updatedAt; // 수정일시

    @Column(name = "created_by", updatable = false)
    private String createdBy; // 생성자

    @Column(name = "updated_by")
    private String updatedBy; // 수정자
}
