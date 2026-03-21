package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.TableCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.TableUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.TableResponse;
import com.restaurant.BeefChefBackend.entity.Tables;
import com.restaurant.BeefChefBackend.enums.TableStatus;
import com.restaurant.BeefChefBackend.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepository;

    private TableResponse toResponse(Tables table){
        return TableResponse.builder()
                .tableId(table.getTableId())
                .tableName(table.getTableName())
                .tableCapacity(table.getTableCapacity())
                .tableStatus(table.getTableStatus())
                .build();
    }

    //create table
    public TableResponse createTable(TableCreateRequest request){
        Tables table = new Tables();
        table.setTableName(request.getTableName());
        table.setTableCapacity(request.getTableCapacity());
        table.setTableStatus(TableStatus.AVAILABLE);
        Tables save = tableRepository.save(table);
        return toResponse(save);
    }

    //Find table findById
    public Tables getTable(Integer id){
        return tableRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Table has id : " + id + " not found!")
        );
    }

    //Get All table
    public List<Tables> getAll(){
        return tableRepository.findAll();
    }


    //update table
    public TableResponse updateTable(Integer id, TableUpdateRequest request){
        Tables table = getTable(id);
        table.setTableName(request.getTableName());
        table.setTableCapacity(request.getTableCapacity());
        table.setTableStatus(TableStatus.AVAILABLE);
        Tables save = tableRepository.save(table);
        return toResponse(save);
    }

    //Del table
    public void deleteTable(Integer id){
        tableRepository.deleteById(id);
    }

}
