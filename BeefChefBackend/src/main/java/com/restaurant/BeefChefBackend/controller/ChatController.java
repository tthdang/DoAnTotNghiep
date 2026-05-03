package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.ChatRequest;
import com.restaurant.BeefChefBackend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping
    public String chat(@RequestBody ChatRequest request){
        try {
            return chatService.chat(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}