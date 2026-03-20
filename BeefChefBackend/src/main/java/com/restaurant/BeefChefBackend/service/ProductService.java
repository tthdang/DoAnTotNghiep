package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.ProductCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ProductResponse;
import com.restaurant.BeefChefBackend.entity.Categories;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import com.restaurant.BeefChefBackend.repository.CategoryRepository;
import com.restaurant.BeefChefBackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private ProductResponse toResponse(Products product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productPrice(product.getProductPrice())
                .productImage(product.getProductImage())
                .productDescription(product.getProductDescription())
                .productStatus(product.getProductStatus())
                .categoryName(product.getCategory().getCategoryName())
                .build();
    }

    //Create product
    public ProductResponse createProduct(ProductCreateRequest request){
        Categories categories = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Category not found!")
        );

        Products products = new Products();
        products.setProductName(request.getProductName());
        products.setProductPrice(request.getProductPrice());
        products.setProductDescription(request.getProductDescription());
        products.setProductStatus(request.getProductStatus());
        products.setProductImage(request.getProductImage());
        products.setCategory(categories);
        Products save = productRepository.save(products);
        return toResponse(save);
    }

    //Get product by id
    public Products getProduct(Integer id){
        return productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product not found!")
        );
    }

    //Get All Products
    public List<Products> getProducts(){
        return productRepository.findAll();
    }

    //Update Product
    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request){

        Categories categories = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Category not found!")
        );

        Products products = getProduct(id);

        products.setProductName(request.getProductName());
        products.setProductPrice(request.getProductPrice());
        products.setProductDescription(request.getProductDescription());
        products.setProductStatus(request.getProductStatus());
        products.setProductDescription(request.getProductDescription());
        products.setProductImage(request.getProductImage());
        products.setCategory(categories);

        Products save = productRepository.save(products);
        return toResponse(save);
    }

    //del product
    public void deleteProduct(Integer id){
        productRepository.deleteById(id);
    }



}
