package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.ProductReportResponse;
import com.restaurant.BeefChefBackend.dto.response.ReportResponse;
import com.restaurant.BeefChefBackend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    //theo ca
    @GetMapping("/shift/{shiftId}")
    public ApiResponse<ReportResponse> getByShift(@PathVariable Integer shiftId){
        try {
            return ApiResponse.<ReportResponse>builder()
                    .result(reportService.getByShift(shiftId))
                    .message("Thống kê theo ca thành công!")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ReportResponse>builder()
                    .message("Lỗi khi thống kê theo ca!" + e.getMessage())
                    .build();
        }
    }

    //theo ngày
    @GetMapping("/day")
    public ApiResponse<ReportResponse> getByDate(@RequestParam String day){
        try {
            return ApiResponse.<ReportResponse>builder()
                    .result(reportService.getByDate(LocalDate.parse(day)))
                    .message("Thồng kê theo ngày thành công!")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ReportResponse>builder()
                    .message("Lỗi khi thống kê theo ngày!" + e.getMessage())
                    .build();
        }
    }

    // theo tuần
    @GetMapping("/week")
    public ApiResponse<ReportResponse> getByWeek(@RequestParam String day){
        try {
            return ApiResponse.<ReportResponse>builder()
                    .result(reportService.getByWeek(LocalDate.parse(day)))
                    .message("Thống kê theo tuần thành công!")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ReportResponse>builder()
                    .message("Lỗi khi thống kê theo tuần!" + e.getMessage())
                    .build();
        }
    }

    //theo tháng
    @GetMapping("/month")
    public ApiResponse<ReportResponse> getByMonth(@RequestParam int year, @RequestParam int month){
        try {
            return ApiResponse.<ReportResponse>builder()
                    .result(reportService.getByMonth(year, month))
                    .message("Thống kê theo tháng thành công!")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ReportResponse>builder()
                    .message("Lỗi khi thống kê theo tháng!" + e.getMessage())
                    .build();
        }
    }



}
