package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.TableCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.TableUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.TableResponse;
import com.restaurant.BeefChefBackend.entity.Tables;
import com.restaurant.BeefChefBackend.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {
    @Autowired
    private TableService service;

    //create
    @PostMapping()
    public ApiResponse<TableResponse> createTable(@RequestBody TableCreateRequest request){
        return ApiResponse.<TableResponse>builder()
                .result(service.createTable(request))
                .message("Table create successfully!")
                .build();
    }

    @GetMapping()
    private ApiResponse<List<TableResponse>> getAllTable(){
        return ApiResponse.<List<TableResponse>>builder()
                .message("Lấy danh sách bàn thành công!")
                .result(service.getAll())
                .build();
    }

    @GetMapping("/{id}")
    private Tables getTableById(@Validated Integer id){
        return service.getTable(id);
    }

    @PutMapping("/{id}")
    private ApiResponse<TableResponse> updateTable(@Validated Integer id, @RequestBody TableUpdateRequest request){
        return ApiResponse.<TableResponse>builder()
                .result(service.updateTable(id, request))
                .message("Update table successfully!")
                .build();
    }

    @DeleteMapping("/{id}")
    public String deleteTable(@PathVariable Integer id){
        service.deleteTable(id);
        return "Table has been deleted!";
    }
}
