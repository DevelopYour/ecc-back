package com.seoultech.ecc.expression;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpressionDto {
    private Integer id;
    private String expression;
    private String description;
}
