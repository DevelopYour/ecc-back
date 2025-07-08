package com.seoultech.ecc.study.datamodel;

import com.seoultech.ecc.admin.dto.EditCategoryDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "topic_category")
public class TopicCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 30)
    private String description;

}
