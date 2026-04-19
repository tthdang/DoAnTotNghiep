package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.PromotionCreateProductRequest;
import com.restaurant.BeefChefBackend.dto.request.PromotionCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.PromotionUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.PromotionResponse;
import com.restaurant.BeefChefBackend.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotion")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @PostMapping("/order")
    public ApiResponse<PromotionResponse> create(@RequestBody PromotionCreateRequest request){
        try {
            return ApiResponse.<PromotionResponse>builder()
                    .message(" Tạo mã khuyến mãi thành công!")
                    .result(promotionService.createOrder(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PromotionResponse>builder()
                    .message(" Tạo mã khuyến mãi thất bại: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/product")
    public ApiResponse<PromotionResponse> createForProduct(@RequestBody PromotionCreateProductRequest request){
        try {
            return ApiResponse.<PromotionResponse>builder()
                    .message(" Tạo mã khuyến mãi thành công!")
                    .result(promotionService.createForproduct(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PromotionResponse>builder()
                    .message(" Tạo mã khuyến mãi thất bại: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping
    public ApiResponse<List<PromotionResponse>> getAll(){
        try {
            return ApiResponse.<List<PromotionResponse>>builder()
                    .message("Lấy tất cả mã khuyến mãi thành công!")
                    .result(promotionService.getAll())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<PromotionResponse>>builder()
                    .message("Lấy tất cả mã khuyến mãi thất bại: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<PromotionResponse> getById(@PathVariable Long id){
        try {
            return ApiResponse.<PromotionResponse>builder()
                    .message("Lấy mã khuyến mãi thành công!")
                    .result(promotionService.getById(id))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PromotionResponse>builder()
                    .message("Lấy mã khuyến mãi thất bại: " + e.getMessage())
                    .build();
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<PromotionResponse> update(@PathVariable Long id, @RequestBody PromotionUpdateRequest request){
        try {
            return ApiResponse.<PromotionResponse>builder()
                    .message("Cập nhật mã khuyến mãi thành công!")
                    .result(promotionService.update(id, request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PromotionResponse>builder()
                    .message("Cập nhật mã khuyến mãi thất bại: " + e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public String deletePromotion(@PathVariable Long id){
        promotionService.deletePromotion(id);
        return "Mã khuyến mãi đã được xoá!";
    }
}
