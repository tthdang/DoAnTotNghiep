package com.restaurant.BeefChefBackend.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.font.FontProvider;
import com.restaurant.BeefChefBackend.dto.response.InvoiceItemResponse;
import com.restaurant.BeefChefBackend.dto.response.InvoiceResponse;
import com.restaurant.BeefChefBackend.entity.Orders;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import com.restaurant.BeefChefBackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfInvoiceService {

    @Autowired
    private OrderRepository orderRepository;

    private InvoiceResponse toInvoiceResponse(Orders order) {
        List<InvoiceItemResponse> items = order.getItem().stream()
                .filter(item -> item.getOrderItemStatus() != OrderItemStatus.CANCEL) // lọc orderItem == cancel
                .map(item -> InvoiceItemResponse.builder()
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getOrderItemQuantity())
                        .unitPrice(item.getOrderItemPrice())
                        .totalPrice(item.getOrderItemPrice()
                                .multiply(BigDecimal.valueOf(item.getOrderItemQuantity())))
                        .build())
                .toList();

        String shiftName = "Ca làm việc không xác định";
        if (order.getShift() != null) {
            shiftName = order.getShift().getShiftName();
        }

        String customerName = order.getUser() != null
                ? order.getUser().getUserLastname() + " " + order.getUser().getUserFirstname()
                : "Khách lẻ";

        // Tạo số hóa đơn đẹp
        String invoiceNumber = generateInvoiceNumber(order);

        BigDecimal subtotal = order.getOrderTotal() != null ? order.getOrderTotal() : BigDecimal.ZERO;
        BigDecimal discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal userRankDiscount = order.getUserRankDiscount() != null ? order.getUserRankDiscount() : BigDecimal.ZERO;
        BigDecimal finalAmount = order.getFinalAmount() != null
                ? order.getFinalAmount()
                : subtotal.subtract(discountAmount).subtract(userRankDiscount);

        return InvoiceResponse.builder()
                .invoiceNumber(invoiceNumber)
                .invoiceDate(LocalDateTime.now())
                .tableName(order.getTable().getTableName())
                .shiftName(shiftName)
                .customerName(customerName)
                .customerPhone(order.getUser() != null ? order.getUser().getUserPhone() : "")
                .orderId(order.getOrderId())
                .paidAt(order.getPaidAt() != null ? order.getPaidAt() : LocalDateTime.now())
                .items(items)
                .subtotal(subtotal)
                .userRankDiscount(userRankDiscount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .paymentMethod("Trực tiếp tại quầy")
                .restaurantName("BEEFCHEF RESTAURANT")
                .restaurantAddress("TDP Hồ Bình, Hoà Bình, Hải Phòng")
                .restaurantPhone("0968 425 403")
                .build();
    }

    // Hàm tạo số hóa đơn đẹp
    private String generateInvoiceNumber(Orders order) {
        LocalDateTime paidTime = order.getPaidAt() != null ? order.getPaidAt() : LocalDateTime.now();
        String dateStr = paidTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "HD" + dateStr + "-" + String.format("%03d", order.getOrderId() % 1000);
    }

    // Hàm chính để sinh PDF
    public byte[] generateInvoicePdf(Integer orderId) throws IOException {
        Orders order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy order " + orderId)
        );

        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Chỉ có thể xuất hóa đơn khi order đã thanh toán!");
        }

        InvoiceResponse invoiceResponse = toInvoiceResponse(order);
        return generateInvoice(invoiceResponse);
    }

    private String buildHtmlTemplate(InvoiceResponse inv) {
        StringBuilder itemsHtml = new StringBuilder();
        int stt = 1;

        for (InvoiceItemResponse item : inv.getItems()) {
            itemsHtml.append(String.format("""
                            <tr>
                                <td style="text-align:center;">%d</td>
                                <td>%s</td>
                                <td style="text-align:center;">%d</td>
                                <td style="text-align:right;">%,.0f ₫</td>
                                <td style="text-align:right;">%,.0f ₫</td>
                            </tr>
                            """,
                    stt++,
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()
            ));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        //hiển thị giảm giá
        String discountHtml = "";
        BigDecimal discount = BigDecimal.ZERO;

        if (inv.getDiscountAmount() != null && inv.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            discount = inv.getDiscountAmount();
            discountHtml = String.format("""
                    <p style="text-align:right; color:#d32f2f; margin: 5px 0;">
                        Giảm giá: <strong>- %, .0f ₫</strong>
                    </p>
                    """, discount);
        }

        // Nếu có giảm giá theo rank thì hiển thị thêm
        if (inv.getUserRankDiscount() != null && inv.getUserRankDiscount().compareTo(BigDecimal.ZERO) > 0) {
            discountHtml += String.format("""
                    <p style="text-align:right; color:#d32f2f; margin: 5px 0; font-size: 0.95em;">
                        (Giảm theo hạng thành viên: - %, .0f ₫)
                    </p>
                    """, inv.getUserRankDiscount());
        }

        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <title>Hóa đơn Beef Chef</title>
                            <style>
                                body { 
                                    font-family: "DejaVu Sans", Arial, sans-serif;
                                    margin: 40px; 
                                    line-height: 1.6; 
                                    color: #333;
                                }
                                .header { 
                                    text-align: center; 
                                    border-bottom: 3px solid #b71c1c; 
                                    padding-bottom: 15px; 
                                    margin-bottom: 25px; 
                                }
                                .logo { 
                                    font-size: 34px; 
                                    font-weight: bold; 
                                    color: #b71c1c; 
                                }
                                table { 
                                    width: 100%%; 
                                    border-collapse: collapse; 
                                    margin: 20px 0; 
                                }
                                th, td { 
                                    border: 1px solid #555; 
                                    padding: 10px 8px; 
                                }
                                th { 
                                    background-color: #ffebee; 
                                    font-weight: bold;
                                }
                                .right { text-align: right; }
                                .total { 
                                    font-weight: bold; 
                                    font-size: 1.25em; 
                                    background-color: #fff3e0; 
                                    padding: 8px;
                                }
                                .footer { 
                                    margin-top: 40px; 
                                    text-align: center; 
                                    color: #555; 
                                }
                            </style>
                        </head>
                        <body>
                            <div class="header">
                                <div class="logo">BEEF CHEF</div>
                                <p>%s</p>
                                <p>%s</p>
                                <p>Hotline: %s</p>
                                <h2>HÓA ĐƠN THANH TOÁN</h2>
                                <p><strong>Số hóa đơn:</strong> %s — <strong>Bàn:</strong> %s</p>
                            </div>
                        
                            <p><strong>Ca làm việc:</strong> %s</p>
                            <p><strong>Khách hàng:</strong> %s</p>
                            <p><strong>SĐT:</strong> %s</p>
                            <p><strong>Thời gian thanh toán:</strong> %s</p>
                        
                            <table>
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Tên món</th>
                                        <th>SL</th>
                                        <th>Đơn giá</th>
                                        <th>Thành tiền</th>
                                    </tr>
                                </thead>
                                <tbody>%s</tbody>
                            </table>
                        
                            <div class="right">
                                <p>Tổng tiền món: <strong>%,.0f ₫</strong></p>
                                %s
                                <p class="total">
                                    TỔNG THANH TOÁN: <strong>%,.0f ₫</strong>
                                </p>
                                <p>Hình thức thanh toán: %s</p>
                            </div>
                        
                            <div class="footer">
                                <p>Cảm ơn quý khách đã dùng bữa tại Beef Chef Restaurant!</p>
                                <p>Hẹn gặp lại quý khách!</p>
                                <p>Chúc quý khách một ngày tốt lành!</p>
                            </div>
                        </body>
                        </html>
                        """,
                inv.getRestaurantName(),
                inv.getRestaurantAddress(),
                inv.getRestaurantPhone(),
                inv.getInvoiceNumber(),
                inv.getTableName(),
                inv.getShiftName(),
                inv.getCustomerName(),
                inv.getCustomerPhone(),
                inv.getPaidAt().format(formatter),
                itemsHtml.toString(),
                inv.getSubtotal(),
                discountHtml,
                inv.getFinalAmount(),
                inv.getPaymentMethod()
        );
    }

    // Tạo PDF từ InvoiceResponse
    private byte[] generateInvoice(InvoiceResponse response) throws IOException {
        String htmlContent = buildHtmlTemplate(response);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ConverterProperties properties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, false);
            properties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(htmlContent, baos, properties);

            byte[] pdfBytes = baos.toByteArray();

            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            //lưu file vào thư mục
            String folderPath = "D:\\Nam4\\DoAn\\DoAnTotNghiep-main\\InvoicePDF";
            String fileName = "HoaDon_HD" + dateStr+ "_" + response.getOrderId() + ".pdf";

            Path directory = Paths.get(folderPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);   // tự động tạo thư mục nếu chưa có
            }

            Path filePath = directory.resolve(fileName);
            Files.write(filePath, pdfBytes);

            System.out.println("Đã lưu hoá đơn tại: " + filePath.toAbsolutePath());

            return pdfBytes;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi tạo/lưu PDF: " + e.getMessage(), e);
        }
    }


}