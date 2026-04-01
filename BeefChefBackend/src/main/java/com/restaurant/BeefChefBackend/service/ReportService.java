package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.response.ProductReportResponse;
import com.restaurant.BeefChefBackend.dto.response.ReportResponse;
import com.restaurant.BeefChefBackend.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    // thống kê theo ca
    public ReportResponse getByShift(Integer shiftId){
        List<ProductReportResponse> list = orderItemRepository.statisticByShift(shiftId);

        BigDecimal total = list.stream()
                .map(ProductReportResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportResponse.builder()
                .listProductReportResponses(list)
                .totalReport(total)
                .build();
    }

    //thống kê theo ngày
    public ReportResponse getByDate(LocalDate date){

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23,59,59);

        List<ProductReportResponse> list = orderItemRepository.statisticByTime(start, end);

        BigDecimal total = list.stream()
                .map(ProductReportResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportResponse.builder()
                .listProductReportResponses(list)
                .totalReport(total)
                .build();
    }

    //thống kê theo tuần
    public ReportResponse getByWeek(LocalDate date){

        LocalDate startDate = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        LocalDate endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<ProductReportResponse> list = orderItemRepository.statisticByTime(startDate.atStartOfDay(),
                endDate.atTime(23,59,59));

        BigDecimal total = list.stream()
                .map(ProductReportResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("Start: " + startDate);
        System.out.println("End: " + endDate);

        return ReportResponse.builder()
                .listProductReportResponses(list)
                .totalReport(total)
                .build();
    }

    // thống kê theo tháng
    public ReportResponse getByMonth(int year, int month){

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<ProductReportResponse> list = orderItemRepository.statisticByTime(start.atStartOfDay(),
                end.atTime(23,59,59));

        BigDecimal total = list.stream()
                .map(ProductReportResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportResponse.builder()
                .listProductReportResponses(list)
                .totalReport(total)
                .build();
    }

}
