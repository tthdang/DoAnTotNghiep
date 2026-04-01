package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.ShiftCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.ShiftUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.ShiftResponse;
import com.restaurant.BeefChefBackend.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shift")
public class ShiftController {
    @Autowired
    private ShiftService service;

    @PostMapping
    public ApiResponse<ShiftResponse> createShift(@RequestBody ShiftCreateRequest request){
        try {
            return ApiResponse.<ShiftResponse>builder()
                    .message("Create shift successfully!")
                    .result(service.createShift(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ShiftResponse>builder()
                    .message("Lỗi khi tạo shift " + e.getMessage())
                    .build();
        }
    }

    @GetMapping
    public ApiResponse<List<ShiftResponse>> getAllShift(){
        try {
            return ApiResponse.<List<ShiftResponse>>builder()
                    .message("Get All shift successfully!")
                    .result(service.getShifts())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ShiftResponse>>builder()
                    .message("Lỗi khi tạo shift " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{shiftId}")
    public ApiResponse<ShiftResponse> getShift(@PathVariable Integer shiftId){
        try {
            return ApiResponse.<ShiftResponse>builder()
                    .message("Get Shift successfully!")
                    .result(service.toResponse(service.getShiftById(shiftId)))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ShiftResponse>builder()
                    .message("Lỗi khi lấy shift " + e.getMessage())
                    .build();
        }
    }

    @PutMapping("/{shiftId}")
    public ApiResponse<ShiftResponse> updateShift(@PathVariable Integer shiftId, @RequestBody ShiftUpdateRequest request){
        try{
            return ApiResponse.<ShiftResponse>builder()
                    .result(service.updateShift(shiftId, request))
                    .message("Update shift successfully!")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ShiftResponse>builder()
                    .message("Lỗi khi update shift!")
                    .build();
        }
    }

    @DeleteMapping("/{shiftId}")
    public String deleteShift(@PathVariable Integer shiftId){
        service.deleteShift(shiftId);
        return "Shift has been deleted!";
    }

}
