package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.ProductCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateRequest;
import com.restaurant.BeefChefBackend.dto.request.ProductUpdateStockRequest;
import com.restaurant.BeefChefBackend.dto.request.RecipeRequest;
import com.restaurant.BeefChefBackend.dto.response.ProductResponse;
import com.restaurant.BeefChefBackend.dto.response.RecipeResponse;
import com.restaurant.BeefChefBackend.entity.Categories;
import com.restaurant.BeefChefBackend.entity.Ingredient;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.entity.Recipe;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import com.restaurant.BeefChefBackend.repository.CategoryRepository;
import com.restaurant.BeefChefBackend.repository.IngredientRepository;
import com.restaurant.BeefChefBackend.repository.ProductRepository;
import com.restaurant.BeefChefBackend.repository.RecipeRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    private ProductResponse toResponse(Products product) {
        int stock = calculateStock(product);

        List<RecipeResponse> recipeResponses = product.getRecipes().stream()
                .map(r -> RecipeResponse.builder()
                        .ingredientId(r.getIngredient().getIngredientId())
                        .ingredientName(r.getIngredient().getIngredientName())
                        .quantityNeeded(r.getQuantityNeeded())
                        .unit(r.getIngredient().getUnit())
                        .build())
                .distinct()
                .toList();
        ProductStatus status = ProductStatus.AVAILABLE;

        if(stock < 1 ){
            status = ProductStatus.OUT_OF_STOCK;
        }

        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productPrice(product.getProductPrice())
                .productImage(product.getProductImage())
                .productDescription(product.getProductDescription())
                .productStatus(status)
                .productStock(stock)
                .productSold(product.getProductSold())
                .categoryName(product.getCategory().getCategoryName())
                .recipes(recipeResponses)
                .build();
    }

    //Create product
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request){
        Categories categories = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy danh mục!")
        );

        Products products = new Products();
        products.setProductName(request.getProductName());
        products.setProductPrice(request.getProductPrice());
        products.setProductDescription(request.getProductDescription());
        products.setProductStatus(request.getProductStatus());
        products.setProductImage(request.getProductImage());
        products.setProductSold(0);
        products.setCategory(categories);

        //lưu trước lấy id
        Products save = productRepository.save(products);
        System.out.println("Recipes: " + request.getRecipes());
        if (request.getRecipes() != null && !request.getRecipes().isEmpty()) {
            List<Recipe> recipeList = new ArrayList<>();
            for (RecipeRequest r : request.getRecipes()) {
                System.out.println("Checking ingredient: " + r.getIngredientId());
                Ingredient ingredient = ingredientRepository.findById(r.getIngredientId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu!"));

                Recipe recipe = new Recipe();
                recipe.setProduct(save);
                recipe.setIngredient(ingredient);
                recipe.setQuantityNeeded(r.getQuantityNeeded());

                recipeList.add(recipe);
            }
            recipeRepository.saveAll(recipeList);
            save.setRecipes(recipeList);
        }


        return toResponse(save);
    }

    //Get product by id
    public Products getProduct(Integer id){
        return productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product not found!")
        );
    }

    //Get All Products
    public List<ProductResponse> getProducts(){
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //Update Product
    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request){

        Categories categories = categoryRepository.findById(request.getCategory().getCategoryId()).orElseThrow(
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

//    // Update lại stock
//    @Transactional
//    public Products decreaseStock(Integer productId, Integer quantity) {
//        Products product = productRepository.findByIdForUpdate(productId);
//
//        if (product == null) {
//            throw new RuntimeException("Product not found");
//        }
//
//        if (product.getProductStock() < quantity) {
//            throw new IllegalArgumentException(
//                    "Món " + product.getProductName() +
//                            " chỉ còn " + product.getProductStock() + " phần!"
//            );
//        }
//        product.setProductStock(product.getProductStock() - quantity);
//        if(product.getProductStock() == 0){
//            product.setProductStatus(ProductStatus.OUT_OF_STOCK);
//        }
//        return productRepository.save(product);
//    }

    //tính sl còn lại của món ăn
    public int calculateStock(Products product) {
        int stock = Integer.MAX_VALUE;
        for (Recipe recipe : product.getRecipes()) {
            //sl nglieu dùng đc
            double available = ingredientService.getAvailable(recipe.getIngredient());
            double needed = recipe.getQuantityNeeded();

            System.out.println("----");
            System.out.println("Nguyên liệu: " + recipe.getIngredient().getIngredientName());
            System.out.println("Available: " + available);
            System.out.println("Needed: " + needed);
            // tính sl có thể làm đc
            int possible = (int) (available / recipe.getQuantityNeeded());
            System.out.println("Possible: " + possible);
            stock = Math.min(stock, possible);
        }
        return stock;
    }

    //Get top 5 san pham ban chay nhat
    public List<ProductResponse> getTop5Products(){
        return productRepository.findTop5ByOrderByProductSoldDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


}
