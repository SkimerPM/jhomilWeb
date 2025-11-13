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
//@CrossOrigin(origins = "http://localhost:5173")
public class CartController {
    private final CartService cartService;
    private final UserService userService;


    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }
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
     * GET /api/v1/cart?sessionId=abc123 (si no está autenticado)
     */
    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam(required = false) String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CartDTO cart;

        // ✅ Si está autenticado → usar usuarioId
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long usuarioId = userService.getUserIdFromAuthentication(auth);
            cart = cartService.getOrCreateCart(usuarioId);
        }
        // ✅ Si NO está autenticado → usar sessionId
        else if (sessionId != null && !sessionId.isEmpty()) {
            cart = cartService.getOrCreateAnonCart(sessionId);
        }
        else {
            throw new IllegalArgumentException("Debes proporcionar sessionId si no estás autenticado");
        }

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

    /**
     * Agrega un item al carrito (CUALQUIER USUARIO - autenticado o no)
     * POST /api/v1/cart/items?sessionId=abc123 (requerido si no autenticado)
     */
    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItem(
            @Valid @RequestBody CreateCartItemRequestDTO request,
            @RequestParam(required = false) String sessionId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CartDTO cart;

        // ✅ Si está autenticado → usar usuarioId
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long usuarioId = userService.getUserIdFromAuthentication(auth);
            cart = cartService.getOrCreateCart(usuarioId);
        }
        // ✅ Si NO está autenticado → usar sessionId
        else if (sessionId != null && !sessionId.isEmpty()) {
            cart = cartService.getOrCreateAnonCart(sessionId);
        }
        else {
            throw new IllegalArgumentException("Debes proporcionar sessionId si no estás autenticado");
        }

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CartDTO cart;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long usuarioId = userService.getUserIdFromAuthentication(auth);
            cart = cartService.getOrCreateCart(usuarioId);
        } else if (sessionId != null && !sessionId.isEmpty()) {
            cart = cartService.getOrCreateAnonCart(sessionId);
        } else {
            throw new IllegalArgumentException("Debes proporcionar sessionId si no estás autenticado");
        }

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CartDTO cart;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long usuarioId = userService.getUserIdFromAuthentication(auth);
            cart = cartService.getOrCreateCart(usuarioId);
        } else if (sessionId != null && !sessionId.isEmpty()) {
            cart = cartService.getOrCreateAnonCart(sessionId);
        } else {
            throw new IllegalArgumentException("Debes proporcionar sessionId si no estás autenticado");
        }

        CartDTO updatedCart = cartService.removeItemFromCart(cart.getId(), itemId);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Vacía el carrito
     * DELETE /api/v1/cart?sessionId=abc123
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam(required = false) String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CartDTO cart;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long usuarioId = userService.getUserIdFromAuthentication(auth);
            cart = cartService.getOrCreateCart(usuarioId);
        } else if (sessionId != null && !sessionId.isEmpty()) {
            cart = cartService.getOrCreateAnonCart(sessionId);
        } else {
            throw new IllegalArgumentException("Debes proporcionar sessionId si no estás autenticado");
        }

        cartService.clearCart(cart.getId());
        return ResponseEntity.noContent().build();
    }

}