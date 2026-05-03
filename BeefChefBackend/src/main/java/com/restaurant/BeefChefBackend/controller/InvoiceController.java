package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.service.PdfInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final PdfInvoiceService pdfInvoiceService;

    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer orderId) {
        try {
            byte[] pdfBytes = pdfInvoiceService.generateInvoicePdf(orderId);

            String fileName = "Hoa-don-HD" + String.format("%08d", orderId) + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", fileName); //Yêu cầu tải về

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Lỗi: " + e.getMessage()).getBytes());

        } catch (IllegalArgumentException e) {
            // Không tìm thấy order
            return ResponseEntity.notFound().build();

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Lỗi khi tạo file PDF".getBytes());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Đã xảy ra lỗi hệ thống".getBytes());
        }
    }
}