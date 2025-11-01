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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * SOLO ADMIN
     */
    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
        List<PurchaseDTO> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable Long id) {
        PurchaseDTO purchase = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchase);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseDTO>> getPurchasesBySupplier(@PathVariable Long supplierId) {
        List<PurchaseDTO> purchases = purchaseService.getPurchasesBySupplier(supplierId);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseDTO>> getPurchasesByStatus(@PathVariable PurchaseStatus status) {
        List<PurchaseDTO> purchases = purchaseService.getPurchasesByStatus(status);
        return ResponseEntity.ok(purchases);
    }

    @PostMapping
    public ResponseEntity<PurchaseDTO> createPurchase(@Valid @RequestBody CreatePurchaseDTO createDTO) {
        PurchaseDTO createdPurchase = purchaseService.createPurchase(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPurchase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDTO> updatePurchase(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseDTO updateDTO) {
        PurchaseDTO updatedPurchase = purchaseService.updatePurchase(id, updateDTO);
        return ResponseEntity.ok(updatedPurchase);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseDTO> updatePurchaseStatus(
            @PathVariable Long id,
            @RequestParam PurchaseStatus status) {
        PurchaseDTO updatedPurchase = purchaseService.updatePurchaseStatus(id, status);
        return ResponseEntity.ok(updatedPurchase);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }
}
