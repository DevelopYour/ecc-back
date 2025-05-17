package com.seoultech.ecc.global;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseDocument {

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Field("created_by")
    private Integer createdBy;  // 변경: String -> Integer (uuid 사용)

    @LastModifiedBy
    @Field("updated_by")
    private Integer updatedBy;  // 변경: String -> Integer (uuid 사용)
}

