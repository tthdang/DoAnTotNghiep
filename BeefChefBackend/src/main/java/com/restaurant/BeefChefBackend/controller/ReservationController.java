package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.ReservationRequest;
import com.restaurant.BeefChefBackend.dto.request.ReservationUpdateStatusRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.ReservationResponse;
import com.restaurant.BeefChefBackend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ApiResponse<ReservationResponse> create(@RequestBody ReservationRequest request){
        try{
            return ApiResponse.<ReservationResponse>builder()
                    .message("Đặt bàn thành công!")
                    .result(reservationService.create(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ReservationResponse>builder()
                    .message("Đặt bàn không thành công: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/available")
    public List<Integer> getUnavailableTables(@RequestParam String dateTime) {
        LocalDateTime time = LocalDateTime.parse(dateTime);

        return reservationService.getUnavailableTableIds(time);
    }

    @GetMapping
    public ApiResponse<List<ReservationResponse>> getAll(){
        try{
            return ApiResponse.<List<ReservationResponse>>builder()
                    .message("Lấy tất cả thông tin đặt bàn thành công!")
                    .result(reservationService.getAll())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ReservationResponse>>builder()
                    .message("Lấy tất cả thông tin đặt bàn không thành công!" + e.getMessage())
                    .build();
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<ReservationResponse> updateStatus(@PathVariable Integer id, @RequestBody ReservationUpdateStatusRequest request) {
        try{
            return ApiResponse.<ReservationResponse>builder()
                    .message("Cập nhật trạng thái yêu cầu đặt bàn thành công!")
                    .result(reservationService.updateStatus(id, request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ReservationResponse>builder()
                    .message("Cập nhật trạng thái yêu cầu đặt bàn không thành công: " +e.getMessage() )
                    .build();
        }
    }
}
