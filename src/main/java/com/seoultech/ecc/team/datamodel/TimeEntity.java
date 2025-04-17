package com.seoultech.ecc.team.datamodel;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time")
public class TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_id")
    private Integer timeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Day day;

    @Column(name = "start_time", nullable = false)
    private int startTime; // ex. 6 → 06:00 ~ 08:00

    public enum Day {
        MON, TUE, WED, THU, FRI, SAT, SUN
    }
}

