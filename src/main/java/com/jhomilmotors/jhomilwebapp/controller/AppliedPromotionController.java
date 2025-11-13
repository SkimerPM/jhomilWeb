package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.entity.AppliedPromotion;
import com.jhomilmotors.jhomilwebapp.service.AppliedPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/applied-promotions")
@RequiredArgsConstructor
public class AppliedPromotionController {
    private final AppliedPromotionService service;

    // Obtener todos los AppliedPromotion
    @GetMapping
    public ResponseEntity<List<AppliedPromotion>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // Obtener promociones aplicadas por pedido
    @GetMapping("/order/{pedidoId}")
    public ResponseEntity<List<AppliedPromotion>> getByOrder(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(service.getByOrderId(pedidoId));
    }

    // Obtener promociones aplicadas por promoción
    @GetMapping("/promotion/{promoId}")
    public ResponseEntity<List<AppliedPromotion>> getByPromotion(@PathVariable Long promoId) {
        return ResponseEntity.ok(service.getByPromotionId(promoId));
    }
}
