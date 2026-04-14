package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.response.ChartResponse;
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
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    // thống kê theo ca
    public ReportResponse getByShiftAndDate(Integer shiftId, LocalDate date){

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23,59,59);

        List<ProductReportResponse> list =
                orderItemRepository.statisticByShiftAndDate(shiftId, start, end);

        BigDecimal total = list.stream()
                .map(ProductReportResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = orderItemRepository
                .countOrdersByShiftAndDate(shiftId, start, end);

        ProductReportResponse bestSeller =
                list.isEmpty() ? null : list.get(0);

        return ReportResponse.builder()
                .listProductReportResponses(list)
                .totalReport(total)
                .totalOrders(totalOrders)
                .bestSeller(bestSeller)
                .build();
    }

    //theo ngày
    public ChartResponse getReportByDay(int month, int year) {

        List<Object[]> data = orderItemRepository.reportByDay(month, year);

        List<String> days = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();

        for (Object[] row : data) {
            days.add("Ngày " + row[0]);
            values.add((BigDecimal) row[1]);
        }

        return new ChartResponse(days, values);
    }

    //theo tuần
    public ChartResponse getReportByWeek(int month, int year) {

        List<Object[]> data = orderItemRepository.reportByWeek(month, year);
        List<String> weekLabels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();

        for (Object[] row : data) {
            int weekOfYear = ((Number) row[0]).intValue();
            BigDecimal amount = (BigDecimal) row[1];

            // Tính tuần trong tháng (1-5)
            LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
            int weekInMonth = calculateWeekOfMonth(firstDayOfMonth, weekOfYear);

            weekLabels.add("Tuần " + weekInMonth);
            values.add(amount);
        }

        return new ChartResponse(weekLabels, values);
    }

    //tính tuần trong tháng
    private int calculateWeekOfMonth(LocalDate firstDayOfMonth, int weekOfYear) {
        int weekOfMonth = 1;
        LocalDate date = firstDayOfMonth;

        while (date.getYear() < firstDayOfMonth.plusMonths(1).getYear() ||
                date.getMonthValue() <= firstDayOfMonth.getMonthValue()) {

            if (date.get(WeekFields.ISO.weekOfWeekBasedYear()) == weekOfYear) {
                return weekOfMonth;
            }

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekOfMonth++;
            }
            date = date.plusDays(1);
        }
        return weekOfMonth;
    }

}
