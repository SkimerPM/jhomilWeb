package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CartDTO;
import com.jhomilmotors.jhomilwebapp.dto.CreateCartItemRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.SyncCartRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateCartItemRequestDTO;
import com.jhomilmotors.jhomilwebapp.service.CartService;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor // Usamos @RequiredArgsConstructor para inyección de dependencias
public class CartController {

    // Inyectados automáticamente gracias a @RequiredArgsConstructor
    private final CartService cartService;
    private final UserService userService;

    // --- Métodos Auxiliares de Lógica de Cart ---

    /**
     * Helper para obtener el carrito basándose en la autenticación o sessionId.
     */
    private CartDTO getCartFromAuthOrSession(String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si está autenticado
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long usuarioId = userService.getUserIdFromAuthentication(auth);
            return cartService.getOrCreateCart(usuarioId);
        }
        // Si NO está autenticado
        else if (sessionId != null && !sessionId.isEmpty()) {
            return cartService.getOrCreateAnonCart(sessionId);
        }
        else {
            throw new IllegalArgumentException("Debes proporcionar sessionId si no estás autenticado.");
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

    /**
     * Obtiene el carrito (autenticado) o anónimo (no autenticado)
     * GET /api/v1/cart?sessionId=abc123
     */
    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam(required = false) String sessionId) {
        CartDTO cart = getCartFromAuthOrSession(sessionId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Obtiene carrito anónimo específico por session ID
     * GET /api/v1/cart/anonymous/{sessionId}
     */
    @GetMapping("/anonymous/{sessionId}")
    public ResponseEntity<CartDTO> getAnonCart(@PathVariable String sessionId) {
        CartDTO cart = cartService.getOrCreateAnonCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    // -----------------------------------------------------------------
    // 🛒 GESTIÓN DE ÍTEMS
    // -----------------------------------------------------------------

    /**
     * Agrega un item al carrito
     * POST /api/v1/cart/items?sessionId=abc123
     */
    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItem(
            @Valid @RequestBody CreateCartItemRequestDTO request,
            @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);

        CartDTO updatedCart = cartService.addItemToCart(cart.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    /**
     * Actualiza la cantidad de un item
     * PUT /api/v1/cart/items/{itemId}?sessionId=abc123
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequestDTO request,
            @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);

        CartDTO updatedCart = cartService.updateCartItem(cart.getId(), itemId, request);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Elimina un item del carrito
     * DELETE /api/v1/cart/items/{itemId}?sessionId=abc123
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);

        CartDTO updatedCart = cartService.removeItemFromCart(cart.getId(), itemId);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Vacía el carrito
     * DELETE /api/v1/cart?sessionId=abc123
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam(required = false) String sessionId) {
        CartDTO cart = getCartFromAuthOrSession(sessionId);

        cartService.clearCart(cart.getId());
        return ResponseEntity.noContent().build();
    }


    // -----------------------------------------------------------------
    // 🏷️ NUEVOS ENDPOINTS PARA CUPONES
    // -----------------------------------------------------------------

    /**
     * Aplica un cupón (promoción de carrito) al carrito.
     * El carrito se identifica por autenticación o sessionId.
     * PUT /api/v1/cart/coupon?sessionId=abc123
     * BODY: { "codigo": "TEST50" }
     */
    @PutMapping("/coupon")
    public ResponseEntity<CartDTO> applyCoupon(@RequestBody CouponRequest request,
                                               @RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);

        CartDTO cartDTO = cartService.applyCoupon(cart.getId(), request.getCodigo());
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * Remueve el cupón aplicado.
     * DELETE /api/v1/cart/coupon?sessionId=abc123
     */
    @DeleteMapping("/coupon")
    public ResponseEntity<CartDTO> removeCoupon(@RequestParam(required = false) String sessionId) {

        CartDTO cart = getCartFromAuthOrSession(sessionId);

        CartDTO cartDTO = cartService.removeCoupon(cart.getId());
        return ResponseEntity.ok(cartDTO);
    }

    // -----------------------------------------------------------------
    // DTO auxiliar para la petición de cupón
    // -----------------------------------------------------------------

    /**
     * DTO interno simple para recibir el código del cupón en el cuerpo de la petición.
     */
    private static class CouponRequest {
        private String codigo;

        // Getters and Setters for Jackson deserialization
        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }
}