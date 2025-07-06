package com.seoultech.ecc.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditCategoryDto {
    private String name;
    private String description;
}
