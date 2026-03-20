package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.ProductCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateRequest;
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
        return ApiResponse.<ProductResponse>builder()
                .message("Create product successfully")
                .result(service.createProduct(request))
                .build();
    }

    @GetMapping
    private List<Products> getProducts(){
        return service.getProducts();
    }

    @GetMapping("/{id}")
    private Products getProduct(@PathVariable Integer id){
        return service.getProduct(id);
    }

    @PutMapping("/{id}")
    private ApiResponse<ProductResponse> updateProduct(@PathVariable Integer id, @RequestBody ProductUpdateRequest request){
        return ApiResponse.<ProductResponse>builder()
                .message("Update product successfully")
                .result(service.updateProduct(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Integer userId){
        service.deleteProduct(userId);
        return "Product has been deleted!";
    }
}
