package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PromotionRepository promotionRepository; // (Opcional si usas el service)
    private final UserRepository userRepository;
    private final PromotionService promotionService;

    // 🚨 CORRECCIÓN: ELIMINADAS LAS VARIABLES GLOBALES (Singleton Issue) 🚨
    // El estado ahora vive en la entidad Cart (base de datos).

    // -------------------------------------------------------------------------
    // 🧩 LÓGICA DE DESCUENTOS DE PRODUCTO (APLICADA AL SNAPSHOT)
    // -------------------------------------------------------------------------

    private BigDecimal calculateItemDiscount(ProductVariant variant, int cantidad) {
        BigDecimal precioBase = variant.getPrecio();

        // Buscamos promo de producto (2x1, precio tachado)
        PromotionDTO promotion = promotionService.findBestActivePromotionByVariantId(variant.getId());

        if (promotion == null) {
            return precioBase.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valorDescuento = promotion.getValorDescuento();
        String tipoDescuento = promotion.getTipoDescuento();

        // 1. Manejo de 2x1
        if ("dos_por_uno".equalsIgnoreCase(tipoDescuento)) {
            if (cantidad >= 2) {
                int pagas = (int) Math.ceil(cantidad / 2.0);
                BigDecimal costoTotal = precioBase.multiply(BigDecimal.valueOf(pagas));
                return costoTotal.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP);
            }
            return precioBase.setScale(2, RoundingMode.HALF_UP);
        }

        // 2. Porcentaje y Monto Fijo
        BigDecimal descuentoMonetario = BigDecimal.ZERO;
        if ("porcentaje".equalsIgnoreCase(tipoDescuento)) {
            BigDecimal porcentaje = valorDescuento.divide(new BigDecimal("100.00"), 4, RoundingMode.HALF_UP);
            descuentoMonetario = precioBase.multiply(porcentaje);
        } else if ("monto_fijo".equalsIgnoreCase(tipoDescuento)) {
            descuentoMonetario = valorDescuento;
        }

        BigDecimal precioFinal = precioBase.subtract(descuentoMonetario);
        return precioFinal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : precioFinal.setScale(2, RoundingMode.HALF_UP);
    }

    // -----------------------------------------------------------------
    // 📦 GESTIÓN BÁSICA DEL CARRITO
    // -----------------------------------------------------------------

    @Transactional
    public CartDTO mergeCartFromClient(Long usuarioId, List<SyncCartRequestDTO.SyncCartItemDTO> itemsFromClient) {
        User user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart userCart = cartRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUsuario(user);
            c.setActivo(true);
            c.setFechaCreacion(LocalDateTime.now());
            c.setItems(new ArrayList<>()); // Inicializar lista
            return cartRepository.save(c);
        });

        // Mapa para actualización rápida
        Map<Long, CartItem> userMap = userCart.getItems().stream()
                .collect(Collectors.toMap(i -> i.getVariante().getId(), i -> i));

        for (SyncCartRequestDTO.SyncCartItemDTO clientItem : itemsFromClient) {
            ProductVariant variant = productVariantRepository.findById(clientItem.getVariantId()).orElse(null);
            if (variant == null) continue;

            if (userMap.containsKey(variant.getId())) {
                CartItem dbItem = userMap.get(variant.getId());
                dbItem.setCantidad(dbItem.getCantidad() + clientItem.getCantidad());
                dbItem.setPrecioUnitarioSnapshot(calculateItemDiscount(variant, dbItem.getCantidad()));
            } else {
                CartItem newItem = new CartItem();
                newItem.setCarrito(userCart);
                newItem.setVariante(variant);
                newItem.setCantidad(clientItem.getCantidad());
                newItem.setPrecioUnitarioSnapshot(calculateItemDiscount(variant, clientItem.getCantidad()));
                userCart.getItems().add(newItem);
            }
        }

        userCart.setFechaActualizacion(LocalDateTime.now());
        // Podríamos revalidar el cupón aquí si existiera
        if (userCart.getCuponCodigo() != null) revalidateCoupon(userCart);

        return convertToDTO(cartRepository.save(userCart));
    }

    @Transactional
    public CartDTO getOrCreateCart(Long usuarioId) {
        Cart cart = cartRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUsuario(userRepository.findById(usuarioId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
                    newCart.setFechaCreacion(LocalDateTime.now());
                    newCart.setActivo(true);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO getOrCreateAnonCart(String sessionId) {
        Cart cart = cartRepository.findFirstBySessionIdAndActivoTrue(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    newCart.setFechaCreacion(LocalDateTime.now());
                    newCart.setActivo(true);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
        return convertToDTO(cart);
    }

    // -----------------------------------------------------------------
    // 🛒 GESTIÓN DE ÍTEMS
    // -----------------------------------------------------------------

    @Transactional
    public CartDTO addItemToCart(Long cartId, CreateCartItemRequestDTO request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        ProductVariant variant = productVariantRepository.findById(request.getVarianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

        if (variant.getStock() < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente.");
        }

        // Buscar en lista en memoria
        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getVariante().getId().equals(variant.getId()))
                .findFirst().orElse(null);

        if (existingItem != null) {
            int newQty = existingItem.getCantidad() + request.getCantidad();
            if (variant.getStock() < newQty) {
                throw new IllegalArgumentException("Stock insuficiente para cantidad total.");
            }
            existingItem.setCantidad(newQty);
            existingItem.setPrecioUnitarioSnapshot(calculateItemDiscount(variant, newQty));
        } else {
            CartItem newItem = new CartItem();
            newItem.setCarrito(cart);
            newItem.setVariante(variant);
            newItem.setCantidad(request.getCantidad());
            newItem.setPrecioUnitarioSnapshot(calculateItemDiscount(variant, request.getCantidad()));
            cart.getItems().add(newItem);
        }

        cart.setFechaActualizacion(LocalDateTime.now());

        // REVALIDACIÓN AUTOMÁTICA DE CUPÓN
        if (cart.getCuponCodigo() != null) revalidateCoupon(cart);

        return convertToDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartDTO updateCartItem(Long cartId, Long itemId, UpdateCartItemRequestDTO request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (item.getVariante().getStock() < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente.");
        }

        item.setCantidad(request.getCantidad());
        // Recalcular precio unitario (ej. si ahora aplica 2x1)
        item.setPrecioUnitarioSnapshot(calculateItemDiscount(item.getVariante(), request.getCantidad()));

        cart.setFechaActualizacion(LocalDateTime.now());
        if (cart.getCuponCodigo() != null) revalidateCoupon(cart);

        return convertToDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartDTO removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        // CORRECCIÓN: Borrado en memoria para orphanRemoval
        cart.getItems().remove(itemToRemove);
        itemToRemove.setCarrito(null);

        cart.setFechaActualizacion(LocalDateTime.now());
        if (cart.getCuponCodigo() != null) revalidateCoupon(cart);

        return convertToDTO(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        // CORRECCIÓN: Limpiar lista para orphanRemoval
        if (cart.getItems() != null) {
            cart.getItems().forEach(item -> item.setCarrito(null));
            cart.getItems().clear();
        }

        // Limpiar estado de cupón también
        cart.setCuponCodigo(null);
        cart.setDescuentoAplicado(BigDecimal.ZERO);

        cart.setFechaActualizacion(LocalDateTime.now());
        cartRepository.save(cart);
    }


    // -------------------------------------------------------------------------
    // 🏷️ LÓGICA DE CUPONES (PERSISTENTE Y CON REGALOS)
    // -------------------------------------------------------------------------

    @Transactional
    public CartDTO applyCoupon(Long cartId, String couponCode) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        // 1. Obtener DTO (Incluye info de regalo si existe)
        PromotionDTO coupon = promotionService.getByCodigo(couponCode);

        // 2. Validaciones básicas
        if (coupon == null || !coupon.getActivo()) {
            throw new IllegalArgumentException("Cupón inválido o expirado.");
        }

        // Si ya había otro cupón aplicado, limpiamos sus efectos (especialmente regalos)
        if (cart.getCuponCodigo() != null && !cart.getCuponCodigo().equals(couponCode)) {
            removeFreeGiftsFromCart(cart, cart.getCuponCodigo());
        }

        // 3. Validar Monto Mínimo
        BigDecimal subtotal = calculateSubtotal(cart);
        if (coupon.getMinCompra() != null && subtotal.compareTo(coupon.getMinCompra()) < 0) {
            throw new IllegalArgumentException("No alcanzas el monto mínimo de compra: " + coupon.getMinCompra());
        }

        // 4. 🎁 APLICAR REGALOS (Si el cupón los tiene)
        if (coupon.getVarianteGratisId() != null) {
            addFreeGiftToCart(cart, coupon.getVarianteGratisId(), coupon.getCantidadGratis());
        }

        // 5. Calcular Descuento Monetario
        BigDecimal descuento = calculateCouponValue(coupon, subtotal);

        // 6. PERSISTENCIA EN BD
        cart.setCuponCodigo(coupon.getCodigo());
        cart.setDescuentoAplicado(descuento);
        cart.setFechaActualizacion(LocalDateTime.now());

        return convertToDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartDTO removeCoupon(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        String codigoAnterior = cart.getCuponCodigo();

        // 1. Limpiar campos de persistencia
        cart.setCuponCodigo(null);
        cart.setDescuentoAplicado(BigDecimal.ZERO);

        // 2. Remover regalos asociados
        if (codigoAnterior != null) {
            removeFreeGiftsFromCart(cart, codigoAnterior);
        }

        cart.setFechaActualizacion(LocalDateTime.now());
        return convertToDTO(cartRepository.save(cart));
    }

    // -------------------------------------------------------------------------
    // ⚙️ MÉTODOS AUXILIARES PRIVADOS (LÓGICA INTERNA)
    // -------------------------------------------------------------------------

    private void revalidateCoupon(Cart cart) {
        try {
            PromotionDTO coupon = promotionService.getByCodigo(cart.getCuponCodigo());
            BigDecimal subtotal = calculateSubtotal(cart);

            // Verificar regla de mínimo de compra
            if (coupon.getMinCompra() != null && subtotal.compareTo(coupon.getMinCompra()) < 0) {
                // Ya no cumple -> Remover cupón
                removeFreeGiftsFromCart(cart, cart.getCuponCodigo());
                cart.setCuponCodigo(null);
                cart.setDescuentoAplicado(BigDecimal.ZERO);
            } else {
                // Sigue cumpliendo -> Recalcular monto
                BigDecimal nuevoDescuento = calculateCouponValue(coupon, subtotal);
                cart.setDescuentoAplicado(nuevoDescuento);
            }
        } catch (Exception e) {
            // Si el cupón ya no existe, limpiar
            cart.setCuponCodigo(null);
            cart.setDescuentoAplicado(BigDecimal.ZERO);
        }
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        // Suma de precios SNAPSHOT (ya tienen descuento de 2x1 aplicado)
        return cart.getItems().stream()
                .map(i -> i.getPrecioUnitarioSnapshot().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateCouponValue(PromotionDTO coupon, BigDecimal subtotal) {
        BigDecimal descuento = BigDecimal.ZERO;
        if ("porcentaje".equalsIgnoreCase(coupon.getTipoDescuento())) {
            BigDecimal factor = coupon.getValorDescuento().divide(new BigDecimal("100.00"), 4, RoundingMode.HALF_UP);
            descuento = subtotal.multiply(factor);
        } else if ("monto_fijo".equalsIgnoreCase(coupon.getTipoDescuento())) {
            descuento = coupon.getValorDescuento();
        }
        // Seguridad: No descontar más que el total
        return descuento.compareTo(subtotal) > 0 ? subtotal : descuento.setScale(2, RoundingMode.HALF_UP);
    }

    private void addFreeGiftToCart(Cart cart, Long variantId, Integer cantidad) {
        // Verificar si ya existe el regalo (precio 0)
        boolean exists = cart.getItems().stream()
                .anyMatch(i -> i.getVariante().getId().equals(variantId)
                        && i.getPrecioUnitarioSnapshot().compareTo(BigDecimal.ZERO) == 0);

        if (!exists) {
            ProductVariant variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Regalo no disponible"));

            CartItem gift = new CartItem();
            gift.setCarrito(cart);
            gift.setVariante(variant);
            gift.setCantidad(cantidad != null ? cantidad : 1);
            gift.setPrecioUnitarioSnapshot(BigDecimal.ZERO); // ¡Gratis!
            cart.getItems().add(gift);
        }
    }

    private void removeFreeGiftsFromCart(Cart cart, String couponCode) {
        try {
            PromotionDTO promo = promotionService.getByCodigo(couponCode);
            if (promo != null && promo.getVarianteGratisId() != null) {
                // Borrar ítem si coincide ID y precio es 0
                cart.getItems().removeIf(i ->
                        i.getVariante().getId().equals(promo.getVarianteGratisId())
                                && i.getPrecioUnitarioSnapshot().compareTo(BigDecimal.ZERO) == 0
                );
            }
        } catch (Exception e) {
            // Ignorar si promo antigua fue borrada
        }
    }

    // -------------------------------------------------------------------------
    // 🔄 CONVERSIÓN A DTO
    // -------------------------------------------------------------------------

    private CartDTO convertToDTO(Cart cart) {
        // 1. Calcular Subtotal
        BigDecimal subtotal = calculateSubtotal(cart);

        // 2. Leer Descuento
        BigDecimal descuentoTotal = cart.getDescuentoAplicado() != null ? cart.getDescuentoAplicado() : BigDecimal.ZERO;

        // 3. Base Imponible (Subtotal - Descuento)
        BigDecimal baseImponible = subtotal.subtract(descuentoTotal);

        // Asegurar que la base imponible no sea negativa
        if (baseImponible.compareTo(BigDecimal.ZERO) < 0) {
            baseImponible = BigDecimal.ZERO;
        }

        // 4. IMPUESTOS A CERO (Lo que quieres)
        BigDecimal impuestos = BigDecimal.ZERO;

        // 5. Costo de envío (Sin cambios)
        BigDecimal costoEnvio = BigDecimal.ZERO;

        // 6. Calcular Total Final (Base Imponible + Impuestos CERO + Envío)
        BigDecimal total = baseImponible.add(impuestos).add(costoEnvio);

        // Mapeo de items (sin cambios)
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());

        // Mapeo de cupones (sin cambios)
        List<CartDTO.CuponAplicadoDTO> cuponesList = new ArrayList<>();
        if (cart.getCuponCodigo() != null) {
            cuponesList.add(CartDTO.CuponAplicadoDTO.builder()
                    .codigo(cart.getCuponCodigo())
                    .nombre("Cupón Aplicado")
                    .descuentoAplicado(descuentoTotal)
                    .build());
        }

        // Construir DTO
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
                .impuestos(impuestos) // Ahora es 0.00
                .costoEnvio(costoEnvio)
                .total(total)
                .cuponesAplicados(cuponesList)
                .build();
    }

    private CartItemDTO convertItemToDTO(CartItem item) {
        ProductVariant variant = item.getVariante();
        Product product = variant.getProduct();

        String imagenUrl = "/images/placeholder.png";

        return CartItemDTO.builder()
                .id(item.getId())
                .carritoId(item.getCarrito().getId())
                .varianteId(variant.getId())
                .varianteSku(variant.getSku())
                .productoNombre(product.getNombre())
                .imagenUrl(imagenUrl)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitarioSnapshot())
                .subtotal(item.getPrecioUnitarioSnapshot().multiply(BigDecimal.valueOf(item.getCantidad())))
                .stockDisponible(variant.getStock())
                .build();
    }
}