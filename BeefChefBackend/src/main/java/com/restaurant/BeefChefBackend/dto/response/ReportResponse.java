package com.restaurant.BeefChefBackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReportResponse {
    private List<ProductReportResponse> listProductReportResponses;
    private BigDecimal totalReport;
}
