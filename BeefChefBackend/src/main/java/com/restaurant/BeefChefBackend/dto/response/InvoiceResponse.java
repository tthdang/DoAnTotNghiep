package com.restaurant.BeefChefBackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InvoiceResponse {
    private String restaurantName = "BEEF CHEF RESTAURANT";
    private String restaurantAddress = "TDP Hồ Bình, Hoà Bình, Hải Phòng";
    private String restaurantPhone = "0968 425 403";

    private String invoiceNumber;
    private LocalDateTime invoiceDate;

    private String tableName;
    private String shiftName;
    private String customerName;
    private String customerPhone;

    private Integer orderId;
    private LocalDateTime paidAt;

    private List<InvoiceItemResponse> items;

    private BigDecimal subtotal;
    private BigDecimal userRankDiscount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    private String paymentMethod = "Trực tiếp tại quầy";
}
