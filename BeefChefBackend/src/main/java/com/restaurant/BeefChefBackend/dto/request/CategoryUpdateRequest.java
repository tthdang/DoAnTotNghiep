package com.restaurant.BeefChefBackend.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryUpdateRequest {
    private String categoryName;
    private String categoryDescription;
}
