package com.seoultech.ecc.team.team_match;

import com.seoultech.ecc.team.datamodel.TimeEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeDto {

    private Integer timeId;
    private String day;
    private int startTime;

    /**
     * TimeEntity를 TimeDto로 변환
     * @param entity TimeEntity 객체
     * @return TimeDto 객체
     */
    public static TimeDto fromEntity(TimeEntity entity) {
        if (entity == null) {
            return null;
        }

        return TimeDto.builder()
                .timeId(entity.getTimeId())
                .day(entity.getDay().name())
                .startTime(entity.getStartTime())
                .build();
    }

    /**
     * 시간 정보를 문자열로 표현 (예: "월요일 19:00-21:00")
     * @return 시간 문자열
     */
    public String getDisplayTime() {
        String dayName = getDayName(this.day);
        String startHour = String.format("%02d", this.startTime);
        String endHour = String.format("%02d", this.startTime + 2);

        return String.format("%s %s:00-%s:00", dayName, startHour, endHour);
    }

    /**
     * Day enum을 한국어로 변환
     * @param day Day enum 문자열
     * @return 한국어 요일명
     */
    private String getDayName(String day) {
        switch (day) {
            case "MON": return "월요일";
            case "TUE": return "화요일";
            case "WED": return "수요일";
            case "THU": return "목요일";
            case "FRI": return "금요일";
            case "SAT": return "토요일";
            case "SUN": return "일요일";
            default: return "알 수 없음";
        }
    }
}
