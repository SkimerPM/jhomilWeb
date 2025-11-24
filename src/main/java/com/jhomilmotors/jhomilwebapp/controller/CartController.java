package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CartDTO;
import com.jhomilmotors.jhomilwebapp.dto.CreateCartItemRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.SyncCartRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateCartItemRequestDTO;
import com.jhomilmotors.jhomilwebapp.service.CartService;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    // --- Métodos Auxiliares de Lógica de Cart ---

    /**
     * Helper para obtener el carrito basándose en la autenticación o sessionId.
     * Prioriza al usuario logueado. Si es anónimo, usa el sessionId.
     */
    private CartDTO getCartFromAuthOrSession(String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 1. Si está autenticado con usuario real
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long usuarioId = userService.getUserIdFromAuthentication(auth);
            return cartService.getOrCreateCart(usuarioId);
        }
        // 2. Si es usuario anónimo (invitado)
        else if (sessionId != null && !sessionId.isEmpty()) {
            return cartService.getOrCreateAnonCart(sessionId);
        }
        else {
            throw new IllegalArgumentException("Session ID requerido para usuarios no autenticados.");
        }
    }


    // -----------------------------------------------------------------
    // 📦 GESTIÓN BÁSICA DEL CARRITO
    // -----------------------------------------------------------------

    @PostMapping("/sync")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> syncCart(@RequestBody SyncCartRequestDTO request,
                                            Authentication auth) {
        Long usuarioId = userService.getUserIdFromAuthentication(auth);
        CartDTO merged = cartService.mergeCartFromClient(usuarioId, request.getItems());
        return ResponseEntity.ok(merged);
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam(required = false) String sessionId) {
        CartDTO cart = getCartFromAuthOrSession(sessionId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/anonymous/{sessionId}")
    public ResponseEntity<CartDTO> getAnonCart(@PathVariable String sessionId) {
        CartDTO cart = cartService.getOrCreateAnonCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    // -----------------------------------------------------------------
    // 🛒 GESTIÓN DE ÍTEMS
    // -----------------------------------------------------------------

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItem(
            @Valid @RequestBody CreateCartItemRequestDTO request,
            @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);
        CartDTO updatedCart = cartService.addItemToCart(cart.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequestDTO request,
            @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);
        CartDTO updatedCart = cartService.updateCartItem(cart.getId(), itemId, request);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);
        CartDTO updatedCart = cartService.removeItemFromCart(cart.getId(), itemId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam(required = false) String sessionId) {
        CartDTO cart = getCartFromAuthOrSession(sessionId);
        cartService.clearCart(cart.getId());
        return ResponseEntity.noContent().build();
    }


    // -----------------------------------------------------------------
    // 🏷️ ENDPOINTS PARA CUPONES (Con Persistencia y Regalos)
    // -----------------------------------------------------------------

    @PutMapping("/coupon")
    public ResponseEntity<CartDTO> applyCoupon(@RequestBody CouponRequest request,
                                               @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);
        // El servicio ahora maneja persistencia y regalos internamente
        CartDTO cartDTO = cartService.applyCoupon(cart.getId(), request.getCodigo());
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<CartDTO> removeCoupon(@RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);
        // El servicio se encarga de limpiar el cupón y los regalos asociados
        CartDTO cartDTO = cartService.removeCoupon(cart.getId());
        return ResponseEntity.ok(cartDTO);
    }

    // -----------------------------------------------------------------
    // DTO auxiliar interno
    // -----------------------------------------------------------------

    @Data // Usamos Lombok para ahorrar código
    private static class CouponRequest {
        private String codigo;
    }
}