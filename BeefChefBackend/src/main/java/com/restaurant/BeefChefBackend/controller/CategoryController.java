package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.CategoryCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.CategoryUpdateRequest;
import com.restaurant.BeefChefBackend.entity.Categories;
import com.restaurant.BeefChefBackend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService service;

    @PostMapping()
    private Categories createCategoey(@RequestBody CategoryCreateRequest request){
        return service.createCatefory(request);
    }

    @GetMapping()
    private List<Categories> getAllCategory(){
        return service.getAllCategory();
    }

    @GetMapping("/{id}")
    private Categories getCategory(@PathVariable Integer id){
        return service.getCategory(id);
    }

    @PutMapping("/{id}")
    private Categories updateCategory(@PathVariable Integer id, @RequestBody CategoryUpdateRequest request){
        return service.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Integer id){
        service.deleteCategory(id);
        return "Category has been deleted!";
    }
}
