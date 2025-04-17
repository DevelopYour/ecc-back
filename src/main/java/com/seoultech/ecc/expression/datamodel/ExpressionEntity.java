package com.seoultech.ecc.expression.datamodel;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expression")
public class ExpressionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false, length = 30)
    private String vocab;

    @Column(nullable = false, length = 30)
    private String meaning;

    @Column(columnDefinition = "int default 0")
    private int count;
}

