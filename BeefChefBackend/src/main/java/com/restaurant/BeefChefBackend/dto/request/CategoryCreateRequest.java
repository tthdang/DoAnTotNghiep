package com.restaurant.BeefChefBackend.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryCreateRequest {
    private String categoryName;
    private String categoryDescription;
}
