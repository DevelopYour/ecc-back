package com.seoultech.ecc.review.datamodel;

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
@Table(name = "review_test_question")
public class ReviewTestQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_test_id", nullable = false)
    private ReviewTestEntity reviewTest;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "is_correct", nullable = false, columnDefinition = "boolean default false")
    private boolean isCorrect;

    @Column(name = "question_order", nullable = false)
    private int questionOrder;
}