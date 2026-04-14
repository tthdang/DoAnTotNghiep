package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.TableCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.TableUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.TableResponse;
import com.restaurant.BeefChefBackend.entity.Orders;
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

    @Autowired
    private OrderService orderService;

    private TableResponse toResponse(Tables table){
        Orders orders = orderService.getCurrentOrderByTableId(table.getTableId());

        return TableResponse.builder()
                .tableId(table.getTableId())
                .tableName(table.getTableName())
                .tableCapacity(table.getTableCapacity())
                .tableStatus(table.getTableStatus())
                .orderId(orders != null ? orders.getOrderId() : null)
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
    public List<TableResponse> getAll(){
        return tableRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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
