package com.seoultech.ecc.member.dto.level;

import com.seoultech.ecc.member.datamodel.LevelChangeRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelChangeRequestDto {

    private Integer id; // Long → Integer 변경
    private Integer memberUuid;
    private String studentId;
    private String memberName;
    private Integer currentLevel;
    private Integer requestedLevel;
    private LevelChangeRequestEntity.RequestStatus status;
    private String reason;
    private LocalDateTime createdAt;

    public static LevelChangeRequestDto fromEntity(LevelChangeRequestEntity entity) {
        return LevelChangeRequestDto.builder()
                .id(entity.getId())
                .memberUuid(entity.getMember().getUuid())
                .studentId(entity.getMember().getStudentId())
                .memberName(entity.getMember().getName())
                .currentLevel(entity.getCurrentLevel())
                .requestedLevel(entity.getRequestedLevel())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}