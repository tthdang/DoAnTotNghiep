package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.ProductCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateStockRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.ProductResponse;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService service;

    @PostMapping
    private ApiResponse<ProductResponse> createProduct(@RequestBody ProductCreateRequest request){
        try {
            return ApiResponse.<ProductResponse>builder()
                    .message("Thêm món ăn thành công!")
                    .result(service.createProduct(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductResponse>builder()
                    .message("Thêm món ăn không thành công! " + e.getMessage())
                    .build();
        }
    }

    @GetMapping
    private ApiResponse<List<ProductResponse>> getProducts(){
        try {
            return ApiResponse.<List<ProductResponse>>builder()
                    .message("Lấy tất cả món ăn thành công!")
                    .result(service.getProducts())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductResponse>>builder()
                    .message("Lấy tất cả món ăn thất bại: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{id}")
    private ApiResponse<ProductResponse> getProduct(@PathVariable Integer id){

        try{
            return ApiResponse.<ProductResponse>builder()
                    .message("Lấy món ăn thành công!")
                    .result(service.getProduct(id))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductResponse>builder()
                    .message("Lấy món ăn tht bại: "+e.getMessage())
                    .build();
        }
    }

    @PutMapping("/{id}")
    private ApiResponse<ProductResponse> updateProduct(@PathVariable Integer id, @RequestBody ProductUpdateRequest request){
        return ApiResponse.<ProductResponse>builder()
                .message("Update product successfully")
                .result(service.updateProduct(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Integer id){
        service.deleteProduct(id);
        return "Product has been deleted!";
    }

    //Get top 5 san pham ban chay
    @GetMapping("/bestSeller")
    public ApiResponse<List<ProductResponse>> getTop5Products(){
        return ApiResponse.<List<ProductResponse>>builder()
                .message("Lấy top 5 sản phẩm thành công!")
                .result(service.getTop5Products())
                .build();
    }
}
