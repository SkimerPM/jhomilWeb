package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CreateReceiptRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.ReceiptDTO;
import com.jhomilmotors.jhomilwebapp.service.ReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor

public class ReceiptController {
    private final ReceiptService receiptService;

    /**
     * ✅ REQUIERE AUTENTICACIÓN
     * Obtiene comprobantes de una orden
     * GET /api/v1/receipts/order/{orderId}
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ReceiptDTO>> getReceiptsByOrder(@PathVariable Long orderId) {
        List<ReceiptDTO> receipts = receiptService.getReceiptsByOrder(orderId);
        return ResponseEntity.ok(receipts);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Crea un comprobante
     * POST /api/v1/admin/receipts
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<ReceiptDTO> createReceipt(
            @Valid @RequestBody CreateReceiptRequestDTO request) {
        ReceiptDTO receipt = receiptService.createReceipt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Anula un comprobante
     * DELETE /api/v1/admin/receipts/{id}
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ReceiptDTO> cancelReceipt(@PathVariable Long id) {
        ReceiptDTO receipt = receiptService.cancelReceipt(id);
        return ResponseEntity.ok(receipt);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Obtiene todos los comprobantes
     * GET /api/v1/admin/receipts?page=0&size=10
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<ReceiptDTO>> getAllReceipts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReceiptDTO> receipts = receiptService.getAllReceipts(pageable);
        return ResponseEntity.ok(receipts);
    }


}