package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.BatchRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.BatchResponse;
import com.restaurant.BeefChefBackend.service.IngredientBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batch")
public class BatchController {
    @Autowired
    private IngredientBatchService ingredientBatchService;

    @PostMapping
    public ApiResponse<BatchResponse> create(@RequestBody BatchRequest request){
        try {
            return ApiResponse.<BatchResponse>builder()
                    .message("Thêm lô nguyên liệu thành công!")
                    .result(ingredientBatchService.create(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<BatchResponse>builder()
                    .message("Thêm lô nguyên liệu thất bại: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping()
    public ApiResponse<List<BatchResponse>> getAll() {
        try{
            return ApiResponse.<List<BatchResponse>>builder()
                    .message("Lấy tất cả lô nguyên liệu thành công!")
                    .result(ingredientBatchService.getAll())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<BatchResponse>>builder()
                    .message("Lấy tất cả lô nguyên liệu thất bại: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/id")
    public ApiResponse<BatchResponse> getById(Integer id){
        try {
            return ApiResponse.<BatchResponse>builder()
                    .message("Lấy lô nguyên liệu thành công!")
                    .result(ingredientBatchService.getById(id))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<BatchResponse>builder()
                    .message("Lấy lô nguyên liệu thất bại: " + e.getMessage())
                    .build();
        }
    }
}
