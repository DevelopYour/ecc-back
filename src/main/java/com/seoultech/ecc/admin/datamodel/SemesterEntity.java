package com.seoultech.ecc.admin.datamodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "semester")
public class SemesterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id")
    private Integer id;

    @Column(nullable = false, length = 10)
    private Integer year;

    @Column(nullable = false, length = 30)
    private Integer semester;
}
