package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.ProductCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateStockRequest;
import com.restaurant.BeefChefBackend.dto.response.ProductResponse;
import com.restaurant.BeefChefBackend.entity.Categories;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import com.restaurant.BeefChefBackend.repository.CategoryRepository;
import com.restaurant.BeefChefBackend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
                .productStock(product.getProductStock())
                .productSold(product.getProductSold())
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
        if (request.getProductStock() != null) {
            products.setProductStock(request.getProductStock());
        }else{
            products.setProductStock(0);
        }
        products.setProductSold(0);
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
        products.setProductStock(request.getProductStock());
        products.setCategory(categories);

        Products save = productRepository.save(products);
        return toResponse(save);
    }

    //del product
    public void deleteProduct(Integer id){
        productRepository.deleteById(id);
    }

    // Update lại stock
    @Transactional
    public Products decreaseStock(Integer productId, Integer quantity) {
        Products product = productRepository.findByIdForUpdate(productId);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        if (product.getProductStock() < quantity) {
            throw new IllegalArgumentException(
                    "Món " + product.getProductName() +
                            " chỉ còn " + product.getProductStock() + " phần!"
            );
        }
        product.setProductStock(product.getProductStock() - quantity);
        if(product.getProductStock() == 0){
            product.setProductStatus(ProductStatus.OUT_OF_STOCK);
        }
        return productRepository.save(product);
    }

    //Get top 5 san pham ban chay nhat
    public List<ProductResponse> getTop5Products(){
        return productRepository.findTop5ByOrderByProductSoldDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


}
