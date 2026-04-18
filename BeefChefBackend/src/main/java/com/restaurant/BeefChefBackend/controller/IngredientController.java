package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.IngredientCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.IngredientUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.IngredientResponse;
import com.restaurant.BeefChefBackend.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    @Autowired
    private IngredientService ingredientService;

    @PostMapping()
    public ApiResponse<IngredientResponse> create(@RequestBody IngredientCreateRequest request){
        try{
            return ApiResponse.<IngredientResponse>builder()
                    .message("Thêm nguyên liệu thành công!")
                    .result(ingredientService.create(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<IngredientResponse>builder()
                    .message("Lỗi khi thêm nguyên liệu: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping
    public ApiResponse<List<IngredientResponse>> getAll(){
        try{
            return ApiResponse.<List<IngredientResponse>>builder()
                    .message("Lấy tất cả nguyên liệu thành công!")
                    .result(ingredientService.getAll())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<IngredientResponse>>builder()
                    .message("Lỗi khi lấy tất cả nguyên liệu: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/id")
    public ApiResponse<IngredientResponse> getById(@PathVariable Integer id){
        try{
            return ApiResponse.<IngredientResponse>builder()
                    .message("Lấy thành công nguyên liệu!")
                    .result(ingredientService.getById(id))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<IngredientResponse>builder()
                    .message("Lấy nguyên liệu thất bại: " + e.getMessage())
                    .build();
        }
    }

    @PutMapping("/id")
    public ApiResponse<IngredientResponse> update(@PathVariable Integer id, @RequestBody IngredientUpdateRequest request){
        try {
            return ApiResponse.<IngredientResponse>builder()
                    .message(" Sửa nguyên liệu thành công!")
                    .result(ingredientService.update(id, request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<IngredientResponse>builder()
                    .message(" Sửa nguyên liệu thất bại: " + e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public String deleteIngredient(@PathVariable Integer id){
            ingredientService.delete(id);
            return "Nguyên liệu đã được xoá!";
    }
}
