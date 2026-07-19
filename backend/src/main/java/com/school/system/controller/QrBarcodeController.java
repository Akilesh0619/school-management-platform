package com.school.system.controller;

import com.school.system.service.QrBarcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "QR & Barcode Module", description = "Endpoints for generating PNG QR codes and Barcodes for ID cards & receipts")
public class QrBarcodeController {

    private final QrBarcodeService qrBarcodeService;

    @GetMapping("/generate/qr")
    @Operation(summary = "Generate QR Code PNG image from content string")
    public ResponseEntity<byte[]> generateQrCode(@RequestParam String text) throws Exception {
        byte[] image = qrBarcodeService.generateQrCodeImage(text, 250, 250);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qrcode.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @GetMapping("/generate/barcode")
    @Operation(summary = "Generate Barcode (Code128) PNG image from code string")
    public ResponseEntity<byte[]> generateBarcode(@RequestParam String text) throws Exception {
        byte[] image = qrBarcodeService.generateBarcodeImage(text, 300, 100);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"barcode.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}
