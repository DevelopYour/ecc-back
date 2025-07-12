package com.seoultech.ecc.study.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "topic")
public class TopicEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_category_id", nullable = false)
    private TopicCategoryEntity category;

    @Column(nullable = false, length = 100)
    private String topic;
}
