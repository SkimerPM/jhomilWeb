package com.jhomilmotors.jhomilwebapp.controller;
import com.jhomilmotors.jhomilwebapp.dto.CreatePaymentRequest;
import com.jhomilmotors.jhomilwebapp.dto.CreatePaymentRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.PaymentDTO;
import com.jhomilmotors.jhomilwebapp.dto.VerifyPaymentRequestDTO;
import com.jhomilmotors.jhomilwebapp.service.MercadoPagoService;
import com.jhomilmotors.jhomilwebapp.service.PaymentService;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import com.mercadopago.resources.preference.Preference;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor

public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;

    @Autowired
    private MercadoPagoService mercadoPagoService;
    @PostMapping("/create/{codigoPedido}")
    public ResponseEntity<?> createPayment(@PathVariable String codigoPedido) {
        try {
            Preference pref = mercadoPagoService.crearPreferencia(codigoPedido);
            return ResponseEntity.ok(Map.of(
                    "preferenceId", pref.getId(),
                    "initPoint", pref.getInitPoint(),
                    "sandboxInitPoint", pref.getSandboxInitPoint()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));

        }
    }



            /**
             * ✅ REQUIERE AUTENTICACIÓN
             * Obtiene pagos de una orden
             * GET /api/v1/payments/order/{orderId}
             */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrder(@PathVariable Long orderId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(payments);
    }

    /**
     * ✅ REQUIERE AUTENTICACIÓN
     * Registra un nuevo pago
     * POST /api/v1/payments
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(
            @Valid @RequestBody CreatePaymentRequestDTO request) {
        PaymentDTO payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Verifica (confirma o rechaza) un pago
     * POST /api/v1/admin/payments/{id}/verify
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/{id}/verify")
    public ResponseEntity<PaymentDTO> verifyPayment(
            @PathVariable Long id,
            @Valid @RequestBody VerifyPaymentRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioVerificadorId = userService.getUserIdFromAuthentication(auth);
        PaymentDTO payment = paymentService.verifyPayment(id, usuarioVerificadorId, request);
        return ResponseEntity.ok(payment);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Obtiene todos los pagos
     * GET /api/v1/admin/payments?page=0&size=10
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<PaymentDTO>> getAllPayments(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentDTO> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Obtiene pagos por estado
     * GET /api/v1/admin/payments/status/CONFIRMADO?page=0&size=10
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/status/{estado}")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByStatus(
            @PathVariable String estado,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentDTO> payments = paymentService.getPaymentsByStatus(estado, pageable);
        return ResponseEntity.ok(payments);
    }



}