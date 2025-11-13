package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CreateOrderRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.OrderDTO;
import com.jhomilmotors.jhomilwebapp.service.CartService;
import com.jhomilmotors.jhomilwebapp.service.OrderService;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor

public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    /**
     * ✅ REQUIERE AUTENTICACIÓN
     * Obtiene los pedidos del usuario autenticado
     * GET /api/v1/orders?page=0&size=10
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getMyOrders(
            @PageableDefault(size = 10) Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = userService.getUserIdFromAuthentication(auth);
        Page<OrderDTO> orders = orderService.getOrdersByUser(usuarioId, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * ✅ REQUIERE AUTENTICACIÓN
     * Obtiene detalles de un pedido
     * GET /api/v1/orders/{id}
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * ✅ REQUIERE AUTENTICACIÓN
     * Crea un nuevo pedido desde el carrito
     * POST /api/v1/orders
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody CreateOrderRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = userService.getUserIdFromAuthentication(auth);

        // Obtener carrito del usuario
        com.jhomilmotors.jhomilwebapp.dto.CartDTO cartDTO = cartService.getOrCreateCart(usuarioId);

        OrderDTO order = orderService.createOrderFromCart(usuarioId, cartDTO.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Obtiene todos los pedidos
     * GET /api/v1/admin/orders?page=0&size=10
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * ✅ REQUIERE ROL ADMIN
     * Cambia el estado de un pedido
     * PATCH /api/v1/admin/orders/{id}/status?estado=PAGADO
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String estado) {
        OrderDTO order = orderService.updateOrderStatus(id, estado);
        return ResponseEntity.ok(order);
    }


}