package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.ChatRequest;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ProductRepository productRepository;

    public String chat(ChatRequest request){

        String message = request.getMessage().toLowerCase();

        //Top 5 món
        if (message.contains("top") || message.contains("nổi bật") || message.contains("những món")) {

            List<Products> top5 = productRepository.findTop5ByOrderByProductSoldDesc();

            String data = top5.stream()
                    .map(p -> p.getProductName() + " ( Đã được gọi:" + p.getProductSold() + " lần). " + p.getProductDescription() +  " - Giá: " + p.getProductPrice())
                    .collect(Collectors.joining(", "));

            return formatWithAI(
                    "Top 5 món bán chạy: " + data,
                    request.getMessage()
            );
        }

        //món bán chạy nhất
        if (message.contains("bán chạy") || message.contains("nhiều nhất")) {

            Products top = productRepository
                    .findTop5ByOrderByProductSoldDesc()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (top == null) return "Hiện chưa có dữ liệu.";

            return formatWithAI(
                    "Món bán chạy nhất là " + top.getProductName() +
                            " với " + top.getProductSold() + " lượt bán" +
                            " giá " + top.getProductPrice() + " VNĐ",
                    request.getMessage()
            );
        }



        return chatClient
                .prompt(request.getMessage())
                .call()
                .content();
    }

    // dùng AI để viết lại cho tự nhiên
    private String formatWithAI(String data, String userQuestion) {

        String prompt = """
        Bạn là chatbot của nhà hàng BeefChef.
        Dữ liệu hệ thống:
        %s
        Hãy trả lời câu hỏi của khách một cách tự nhiên và thân thiện:
        "%s"
        """.formatted(data, userQuestion);

        return chatClient.prompt(prompt).call().content();
    }
}
