package com.restaurant.BeefChefBackend.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatRequest {
    private String message;
}
