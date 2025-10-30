package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CreatePurchaseDTO;
import com.jhomilmotors.jhomilwebapp.dto.PurchaseDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdatePurchaseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Purchase.PurchaseStatus;
import com.jhomilmotors.jhomilwebapp.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard/purchases")
@RequiredArgsConstructor

public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * GET /admin/dashboard/purchases
     * Obtiene todas las compras
     */
    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
        List<PurchaseDTO> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    /**
     * GET /admin/dashboard/purchases/{id}
     * Obtiene una compra por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable Long id) {
        PurchaseDTO purchase = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchase);
    }

    /**
     * GET /admin/dashboard/purchases/supplier/{supplierId}
     * Obtiene todas las compras de un proveedor específico
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseDTO>> getPurchasesBySupplier(@PathVariable Long supplierId) {
        List<PurchaseDTO> purchases = purchaseService.getPurchasesBySupplier(supplierId);
        return ResponseEntity.ok(purchases);
    }

    /**
     * GET /admin/dashboard/purchases/status/{status}
     * Obtiene todas las compras por estado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseDTO>> getPurchasesByStatus(@PathVariable PurchaseStatus status) {
        List<PurchaseDTO> purchases = purchaseService.getPurchasesByStatus(status);
        return ResponseEntity.ok(purchases);
    }

    /**
     * POST /admin/dashboard/purchases
     * Crea una nueva compra con sus items
     */
    @PostMapping
    public ResponseEntity<PurchaseDTO> createPurchase(@Valid @RequestBody CreatePurchaseDTO createDTO) {
        PurchaseDTO createdPurchase = purchaseService.createPurchase(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPurchase);
    }

    /**
     * PUT /admin/dashboard/purchases/{id}
     * Actualiza una compra existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDTO> updatePurchase(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseDTO updateDTO) {
        PurchaseDTO updatedPurchase = purchaseService.updatePurchase(id, updateDTO);
        return ResponseEntity.ok(updatedPurchase);
    }

    /**
     * PATCH /admin/dashboard/{id}/status
     * Actualiza solo el estado de una compra
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseDTO> updatePurchaseStatus(
            @PathVariable Long id,
            @RequestParam PurchaseStatus status) {
        PurchaseDTO updatedPurchase = purchaseService.updatePurchaseStatus(id, status);
        return ResponseEntity.ok(updatedPurchase);
    }

    /**
     * DELETE /admin/dashboard/purchases/{id}
     * Elimina una compra
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }
}
