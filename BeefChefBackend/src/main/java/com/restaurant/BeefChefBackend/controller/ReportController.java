package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.ChartResponse;
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
    @GetMapping("/shiftByDate")
    public ApiResponse<ReportResponse> getByShiftAndDate(@RequestParam Integer shiftId, @RequestParam String date){
        return ApiResponse.<ReportResponse>builder()
                .result(reportService.getByShiftAndDate(shiftId, LocalDate.parse(date)))
                .message("OK")
                .build();
    }

    //theo ngày
    @GetMapping("/day")
    public ApiResponse<ChartResponse> getReportByDay(
            @RequestParam int month,
            @RequestParam int year) {

        return ApiResponse.<ChartResponse>builder()
                .result(reportService.getReportByDay(month, year))
                .message("OK")
                .build();
    }

    // theo tuần
    @GetMapping("/week")
    public ApiResponse<ChartResponse> getReportByWeek(
            @RequestParam int month,
            @RequestParam int year) {

        return ApiResponse.<ChartResponse>builder()
                .result(reportService.getReportByWeek(month, year))
                .message("OK")
                .build();
    }





}
