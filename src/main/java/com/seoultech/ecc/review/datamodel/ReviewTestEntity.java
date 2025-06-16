package com.seoultech.ecc.review.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review_test")
public class ReviewTestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;

    @Column(name = "is_complete", nullable = false, columnDefinition = "boolean default false")
    private boolean isComplete;

    @OneToMany(mappedBy = "reviewTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTestQuestionEntity> questions = new ArrayList<>();

    // 편의 메서드
    public Integer getUserId() {
        return review != null && review.getMember() != null ? review.getMember().getUuid() : null;
    }
}