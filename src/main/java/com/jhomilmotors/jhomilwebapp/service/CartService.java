package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.*;

import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;

import com.jhomilmotors.jhomilwebapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;


    @Transactional
    public CartDTO mergeCartFromClient(Long usuarioId, List<SyncCartRequestDTO.SyncCartItemDTO> itemsFromClient) {
        // 1. Busca el usuario
        User user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 2. Buscar el carrito del usuario
        Cart userCart = cartRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUsuario(user);
            c.setActivo(true);
            c.setFechaCreacion(LocalDateTime.now());
            return cartRepository.save(c);
        });

        Map<String, CartItem> userMap = userCart.getItems() != null ? userCart.getItems().stream()
                .collect(Collectors.toMap(
                        i -> i.getVariante().getId() + "",
                        i -> i
                )) : Map.of();

        for (SyncCartRequestDTO.SyncCartItemDTO clientItem : itemsFromClient) {
            ProductVariant variant = productVariantRepository.findById(clientItem.getVariantId())
                    .orElse(null);
            if (variant == null) continue;
            String key = variant.getId() + "";
            if (userMap.containsKey(key)) {
                CartItem dbItem = userMap.get(key);
                dbItem.setCantidad(dbItem.getCantidad() + clientItem.getCantidad());
                cartItemRepository.save(dbItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.setCarrito(userCart);
                newItem.setVariante(variant);
                newItem.setCantidad(clientItem.getCantidad());
                newItem.setPrecioUnitarioSnapshot(variant.getPrecio());
                cartItemRepository.save(newItem);
                userCart.getItems().add(newItem);
            }
        }

        // Usa tu método convertToDTO para retornar el resultado
        return convertToDTO(userCart);
    }



    /**
     * Obtiene o crea carrito del usuario autenticado
     */
    @Transactional
    public CartDTO getOrCreateCart(Long usuarioId) {

        Cart cart = cartRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUsuario(userRepository.findById(usuarioId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
                    newCart.setFechaCreacion(LocalDateTime.now());
                    newCart.setActivo(true);
                    return cartRepository.save(newCart);
                });
        return convertToDTO(cart);
    }

    /**
     * Obtiene o crea carrito anónimo por sesión
     */
    @Transactional
    public CartDTO getOrCreateAnonCart(String sessionId) {
        Cart cart = cartRepository.findBySessionIdAndActivoTrue(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    newCart.setFechaCreacion(LocalDateTime.now());
                    newCart.setActivo(true);
                    return cartRepository.save(newCart);
                });
        return convertToDTO(cart);
    }

    /**
     * Agrega un item al carrito
     */
    @Transactional
    public CartDTO addItemToCart(Long cartId, CreateCartItemRequestDTO request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        ProductVariant variant = productVariantRepository.findById(request.getVarianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

        // Validar stock
        if (variant.getStock() < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + variant.getStock());
        }

        // Verificar si el item ya existe
        CartItem existingItem = cartItemRepository.findByCarritoIdAndVarianteId(cartId, request.getVarianteId())
                .orElse(null);

        if (existingItem != null) {
            // Actualizar cantidad
            int newQty = existingItem.getCantidad() + request.getCantidad();
            if (variant.getStock() < newQty) {
                throw new IllegalArgumentException("Stock insuficiente para la cantidad solicitada");
            }
            existingItem.setCantidad(newQty);
            cartItemRepository.save(existingItem);
        } else {
            // Crear nuevo item
            CartItem newItem = new CartItem();
            newItem.setCarrito(cart);
            newItem.setVariante(variant);
            newItem.setCantidad(request.getCantidad());
            newItem.setPrecioUnitarioSnapshot(variant.getPrecio());
            cartItemRepository.save(newItem);
        }

        cart.setFechaActualizacion(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Actualiza la cantidad de un item del carrito
     */
    @Transactional
    public CartDTO updateCartItem(Long cartId, Long itemId, UpdateCartItemRequestDTO request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getCarrito().getId().equals(cartId)) {
            throw new IllegalArgumentException("El item no pertenece a este carrito");
        }

        ProductVariant variant = item.getVariante();
        if (variant.getStock() < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + variant.getStock());
        }

        item.setCantidad(request.getCantidad());
        cartItemRepository.save(item);

        cart.setFechaActualizacion(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Elimina un item del carrito
     */
    @Transactional
    public CartDTO removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getCarrito().getId().equals(cartId)) {
            throw new IllegalArgumentException("El item no pertenece a este carrito");
        }

        cartItemRepository.deleteById(itemId);

        cart.setFechaActualizacion(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Vacía el carrito
     */
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        cartItemRepository.deleteByCarritoId(cartId);

        cart.setFechaActualizacion(LocalDateTime.now());
        cartRepository.save(cart);
    }

    /**
     * Convierte entidad Cart a DTO con cálculos de totales
     */
    private CartDTO convertToDTO(Cart cart) {
        List<CartItem> items = cart.getItems() != null ? cart.getItems() : List.of();

        // Calcular subtotal
        BigDecimal subtotal = items.stream()
                .map(item -> item.getPrecioUnitarioSnapshot().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // TODO: Calcular descuento total de cupones aplicados
        BigDecimal descuentoTotal = BigDecimal.ZERO;

        // TODO: Calcular impuestos (ej: 18% IGV)
        BigDecimal impuestos = subtotal.multiply(BigDecimal.valueOf(0.18));

        // TODO: Obtener costo de envío (por ahora 0)
        BigDecimal costoEnvio = BigDecimal.ZERO;

        BigDecimal total = subtotal.subtract(descuentoTotal).add(impuestos).add(costoEnvio);

        List<CartItemDTO> itemDTOs = items.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .id(cart.getId())
                .usuarioId(cart.getUsuario() != null ? cart.getUsuario().getId() : null)
                .sessionId(cart.getSessionId())
                .fechaCreacion(cart.getFechaCreacion())
                .fechaActualizacion(cart.getFechaActualizacion())
                .activo(cart.getActivo())
                .items(itemDTOs)
                .subtotal(subtotal)
                .descuentoTotal(descuentoTotal)
                .impuestos(impuestos)
                .costoEnvio(costoEnvio)
                .total(total)
                .build();
    }

    /**
     * Convierte CartItem a DTO
     */
    private CartItemDTO convertItemToDTO(CartItem item) {
        ProductVariant variant = item.getVariante();
        Product product = variant.getProduct();

        // Obtener imagen principal
        String imagenUrl = "/images/placeholder.png";
        // TODO: Obtener imagen principal de la variante

        BigDecimal subtotal = item.getPrecioUnitarioSnapshot()
                .multiply(BigDecimal.valueOf(item.getCantidad()));

        return CartItemDTO.builder()
                .id(item.getId())
                .carritoId(item.getCarrito().getId())
                .varianteId(variant.getId())
                .varianteSku(variant.getSku())
                .productoNombre(product.getNombre())
                .imagenUrl(imagenUrl)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitarioSnapshot())
                .subtotal(subtotal)
                .stockDisponible(variant.getStock())
                .build();
    }

}