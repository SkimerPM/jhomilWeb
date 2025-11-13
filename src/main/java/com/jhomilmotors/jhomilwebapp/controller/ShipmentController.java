package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CreateShipmentRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.ShipmentDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateShipmentStatusRequestDTO;
import com.jhomilmotors.jhomilwebapp.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    /**
     * ✅ REQUIERE AUTENTICACIÓN (usuario o admin puede ver envío de su orden)
     * Obtiene envío de una orden
     * GET /api/v1/shipments/order/{orderId}
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentDTO> getShipmentByOrder(@PathVariable Long orderId) {
        ShipmentDTO shipment = shipmentService.getShipmentByOrder(orderId);
        return ResponseEntity.ok(shipment);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Crea un envío para una orden
     * POST /api/v1/admin/shipments
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<ShipmentDTO> createShipment(
            @Valid @RequestBody CreateShipmentRequestDTO request) {
        ShipmentDTO shipment = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Actualiza el estado de un envío
     * PATCH /api/v1/admin/shipments/{id}/status
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShipmentStatusRequestDTO request) {
        ShipmentDTO shipment = shipmentService.updateShipmentStatus(id, request);
        return ResponseEntity.ok(shipment);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Obtiene todos los envíos
     * GET /api/v1/admin/shipments?page=0&size=10
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<ShipmentDTO>> getAllShipments(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ShipmentDTO> shipments = shipmentService.getAllShipments(pageable);
        return ResponseEntity.ok(shipments);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Obtiene envíos por estado
     * GET /api/v1/admin/shipments/status/EN_TRANSITO?page=0&size=10
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/status/{estado}")
    public ResponseEntity<Page<ShipmentDTO>> getShipmentsByStatus(
            @PathVariable String estado,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ShipmentDTO> shipments = shipmentService.getShipmentsByStatus(estado, pageable);
        return ResponseEntity.ok(shipments);
    }


}