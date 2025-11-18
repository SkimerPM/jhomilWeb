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
    private final PromotionService promotionService;


    // 🚨 VARIABLES TEMPORALES PARA EL DESCUENTO DE CUPÓN (SIN MODIFICAR MODELO) 🚨
    private BigDecimal lastAppliedCouponDiscount = BigDecimal.ZERO;
    private PromotionDTO lastAppliedCoupon = null;


    // -------------------------------------------------------------------------
    // 🧩 LÓGICA DE DESCUENTOS DE PRODUCTO (APLICADA AL SNAPSHOT)
    // -------------------------------------------------------------------------

    /**
     * Consulta y aplica la mejor promoción de producto vigente para una variante o su producto.
     * Devuelve el precio unitario final de venta con descuento (si aplica).
     * @param item El CartItem que se está evaluando/creando.
     * @return El precio unitario final (precio unitario - descuento).
     */
    private BigDecimal calculateItemDiscount(CartItem item) {
        ProductVariant variant = item.getVariante();
        BigDecimal precioBase = variant.getPrecio();

        // Usamos el PromotionService para encontrar la promo
        PromotionDTO promotion = promotionService.findBestActivePromotionByVariantId(variant.getId());

        if (promotion == null) {
            return precioBase.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valorDescuento = promotion.getValorDescuento();
        String tipoDescuento = promotion.getTipoDescuento();

        // 1. Manejo de 2x1 (Lógica de ajuste del precio unitario efectivo)
        if ("dos_por_uno".equalsIgnoreCase(tipoDescuento)) {
            int cantidad = item.getCantidad();

            if (cantidad >= 2) {
                // Paga 1, lleva 2. Si lleva 3, paga 2, lleva 3. Si lleva 4, paga 2, lleva 4.
                int pagas = (int) Math.ceil(cantidad / 2.0);
                int llevas = cantidad;

                BigDecimal costoTotal = precioBase.multiply(BigDecimal.valueOf(pagas));

                // Precio unitario efectivo: costo total / cantidad que llevas
                return costoTotal.divide(
                        BigDecimal.valueOf(llevas), 2, RoundingMode.HALF_UP
                );
            }
            return precioBase.setScale(2, RoundingMode.HALF_UP);
        }

        // 2. Manejo de Porcentaje y Monto Fijo
        BigDecimal descuentoMonetario = BigDecimal.ZERO;

        if ("porcentaje".equalsIgnoreCase(tipoDescuento)) {
            BigDecimal porcentaje = valorDescuento.divide(new BigDecimal("100.00"), 4, RoundingMode.HALF_UP);
            descuentoMonetario = precioBase.multiply(porcentaje);

        } else if ("monto_fijo".equalsIgnoreCase(tipoDescuento)) {
            descuentoMonetario = valorDescuento;
        }

        // 3. Devolver el precio final unitario
        BigDecimal precioFinal = precioBase.subtract(descuentoMonetario);

        return precioFinal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : precioFinal.setScale(2, RoundingMode.HALF_UP);
    }

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

        if (variant.getStock() < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + variant.getStock());
        }

        CartItem tempItem = new CartItem();
        tempItem.setVariante(variant);
        tempItem.setCantidad(request.getCantidad());

        // 🚨 Calcular precio con descuento de producto 🚨
        BigDecimal precioFinal = calculateItemDiscount(tempItem);

        CartItem existingItem = cartItemRepository.findByCarritoIdAndVarianteId(cartId, request.getVarianteId())
                .orElse(null);

        if (existingItem != null) {
            int newQty = existingItem.getCantidad() + request.getCantidad();
            if (variant.getStock() < newQty) {
                throw new IllegalArgumentException("Stock insuficiente para la cantidad solicitada");
            }
            existingItem.setCantidad(newQty);
            existingItem.setPrecioUnitarioSnapshot(precioFinal); // Actualiza el snapshot por si cambió la promo
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCarrito(cart);
            newItem.setVariante(variant);
            newItem.setCantidad(request.getCantidad());
            newItem.setPrecioUnitarioSnapshot(precioFinal);
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

        // 🚨 Recalcular el snapshot por si el cambio de cantidad afecta al 2x1 🚨
        CartItem tempItem = new CartItem();
        tempItem.setVariante(variant);
        tempItem.setCantidad(request.getCantidad());
        item.setPrecioUnitarioSnapshot(calculateItemDiscount(tempItem));

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


    // -------------------------------------------------------------------------
    // 🧩 LÓGICA DE DESCUENTO DE CUPÓN (APLICADA AL TOTAL)
    // -------------------------------------------------------------------------

    @Transactional
    public CartDTO applyCoupon(Long cartId, String couponCode) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        PromotionDTO coupon = promotionService.getByCodigo(couponCode); // Lanza excepción si no existe

        // Si no está activo o no es un cupón aplicable al carrito (podrías añadir una regla aquí)
        if (!coupon.getActivo() || coupon.getCodigo() == null) {
            this.lastAppliedCouponDiscount = BigDecimal.ZERO;
            this.lastAppliedCoupon = null;
            throw new IllegalArgumentException("Cupón inválido o no vigente.");
        }

        // Recalcular subtotal actual (Base para cupones porcentuales/mínimo de compra)
        BigDecimal subtotalNetoItems = cart.getItems().stream()
                .map(item -> item.getPrecioUnitarioSnapshot().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aplicar reglas de cupón
        if (coupon.getMinCompra() != null && subtotalNetoItems.compareTo(coupon.getMinCompra()) < 0) {
            this.lastAppliedCouponDiscount = BigDecimal.ZERO;
            this.lastAppliedCoupon = null;
            throw new IllegalArgumentException("El subtotal no alcanza el mínimo de compra requerido (" + coupon.getMinCompra() + ").");
        }

        String tipoDescuento = coupon.getTipoDescuento();
        BigDecimal valor = coupon.getValorDescuento();
        BigDecimal descuentoAplicado = BigDecimal.ZERO;

        if ("porcentaje".equalsIgnoreCase(tipoDescuento)) {
            BigDecimal porcentaje = valor.divide(new BigDecimal("100.00"), 4, RoundingMode.HALF_UP);
            descuentoAplicado = subtotalNetoItems.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);

        } else if ("monto_fijo".equalsIgnoreCase(tipoDescuento)) {
            descuentoAplicado = valor.setScale(2, RoundingMode.HALF_UP);

        } else {
            // 2x1, etc., no son cupones de carrito
            throw new IllegalArgumentException("Tipo de cupón no aplicable a nivel de carrito.");
        }

        // Guardar descuento en la variable temporal
        this.lastAppliedCouponDiscount = descuentoAplicado;
        this.lastAppliedCoupon = coupon;

        cart.setFechaActualizacion(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO removeCoupon(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        this.lastAppliedCouponDiscount = BigDecimal.ZERO;
        this.lastAppliedCoupon = null;

        cart.setFechaActualizacion(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Convierte entidad Cart a DTO con cálculos de totales
     */
    private CartDTO convertToDTO(Cart cart) {
        List<CartItem> items = cart.getItems() != null ? cart.getItems() : cartItemRepository.findByCarritoId(cart.getId());

        // 1. Subtotal Neto de Ítems (ya incluye el descuento de producto/2x1)
        BigDecimal subtotalNetoItems = items.stream()
                .map(item -> item.getPrecioUnitarioSnapshot().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        // 2. Descuento Total (Descuento de Cupón)
        BigDecimal descuentoTotal = this.lastAppliedCouponDiscount.setScale(2, RoundingMode.HALF_UP);

        // 3. Base Imponible: Subtotal menos Descuento de Cupón
        BigDecimal baseImponible = subtotalNetoItems.subtract(descuentoTotal);
        if (baseImponible.compareTo(BigDecimal.ZERO) < 0) {
            baseImponible = BigDecimal.ZERO;
        }

        // 4. Impuestos (ej: 18% IGV)
        BigDecimal impuestos = baseImponible.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);

        // 5. Costo de envío
        BigDecimal costoEnvio = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // 6. Total
        BigDecimal total = baseImponible.add(impuestos).add(costoEnvio).setScale(2, RoundingMode.HALF_UP);

        List<CartItemDTO> itemDTOs = items.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());

        List<CartDTO.CuponAplicadoDTO> cuponesAplicados = List.of();
        if (this.lastAppliedCoupon != null && this.lastAppliedCouponDiscount.compareTo(BigDecimal.ZERO) > 0) {
            cuponesAplicados = List.of(CartDTO.CuponAplicadoDTO.builder()
                    .codigo(this.lastAppliedCoupon.getCodigo())
                    .nombre(this.lastAppliedCoupon.getNombre())
                    .descuentoAplicado(this.lastAppliedCouponDiscount)
                    .build());
        }

        return CartDTO.builder()
                .id(cart.getId())
                .usuarioId(cart.getUsuario() != null ? cart.getUsuario().getId() : null)
                .sessionId(cart.getSessionId())
                .fechaCreacion(cart.getFechaCreacion())
                .fechaActualizacion(cart.getFechaActualizacion())
                .activo(cart.getActivo())
                .items(itemDTOs)
                .subtotal(subtotalNetoItems)
                .descuentoTotal(descuentoTotal)
                .impuestos(impuestos)
                .costoEnvio(costoEnvio)
                .total(total)
                .cuponesAplicados(cuponesAplicados)
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