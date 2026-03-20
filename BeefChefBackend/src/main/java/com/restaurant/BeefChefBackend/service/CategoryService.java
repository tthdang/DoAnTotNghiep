package com.restaurant.BeefChefBackend.service;


import com.restaurant.BeefChefBackend.dto.request.CategoryCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.CategoryUpdateRequest;
import com.restaurant.BeefChefBackend.entity.Categories;
import com.restaurant.BeefChefBackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    //tạo danh mục
    public Categories createCatefory(CategoryCreateRequest request){
        Categories categories = new Categories();
        categories.setCategoryName(request.getCategoryName());
        categories.setCategoryDescription(request.getCategoryDescription());
        return categoryRepository.save(categories);
    }

    public List<Categories> getAllCategory(){
        return categoryRepository.findAll();
    }

    //Tìm danh mục
    public Categories getCategory(Integer id){
        return categoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Category not found!")
        );
    }

    //Update Danh mục
    public Categories updateCategory(Integer id, CategoryUpdateRequest request){
        Categories category = getCategory(id);

        category.setCategoryName(request.getCategoryName());
        category.setCategoryDescription(request.getCategoryDescription());
        return categoryRepository.save(category);
    }

    //Del danh mục
    public void deleteCategory(Integer id){
        categoryRepository.deleteById(id);

    }

}
