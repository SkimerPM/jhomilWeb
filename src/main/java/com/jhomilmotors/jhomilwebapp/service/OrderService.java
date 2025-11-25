package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CreateOrderRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.OrderDTO;
import com.jhomilmotors.jhomilwebapp.dto.OrderItemDTO;

import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.enums.OrderStatus;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;

import com.jhomilmotors.jhomilwebapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AppliedPromotionRepository appliedPromotionRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;


    /**
     * Crea un pedido desde el carrito (CORREGIDO)
     */
    @Transactional
    public OrderDTO createOrderFromCart(Long usuarioId, Long cartId, CreateOrderRequestDTO request) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        // Generar código de pedido único
        String codigo = "PED-" + System.currentTimeMillis();

        // -------------------------------------------------------
        // 1. 🧮 CALCULADORA: Sumar los items ANTES de crear el pedido
        // -------------------------------------------------------
        BigDecimal subtotalCalculado = cart.getItems().stream()
                .map(item -> item.getPrecioUnitarioSnapshot().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = BigDecimal.ZERO; // TODO: Obtener de cupones
        BigDecimal impuestos = BigDecimal.ZERO; // TODO: Configuración
        BigDecimal costoEnvio = BigDecimal.ZERO; // TODO: Tarifa envío

        // Total Final Real
        BigDecimal totalCalculado = subtotalCalculado.add(impuestos).add(costoEnvio).subtract(descuento);
        // -------------------------------------------------------

        // Crear pedido con los VALORES CALCULADOS
        Order order = Order.builder()
                .usuario(usuario)
                .codigo(codigo)
                .fechaPedido(LocalDateTime.now())
                .estado(OrderStatus.PENDIENTE)
                .subtotal(subtotalCalculado) // <--- ¡AQUÍ USAMOS EL VALOR REAL!
                .descuento(descuento)
                .impuestos(impuestos)
                .costoEnvio(costoEnvio)
                .total(totalCalculado)       // <--- ¡AQUÍ TAMBIÉN!
                .metodoPago(request.getMetodoPago())
                .direccionEnvio(request.getDireccionEnvio())
                .nota(request.getNota())
                .items(List.of())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Crear items del pedido y mover inventario
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    ProductVariant variant = cartItem.getVariante();

                    // Seleccionar un lote disponible
                    Batch batch = batchRepository.findWithAvailableStock().stream()
                            .filter(b -> b.getVariante().getId().equals(variant.getId()))
                            .filter(b -> b.getCantidadDisponible() >= cartItem.getCantidad())
                            .findFirst()
                            .orElse(null);

                    OrderItem orderItem = OrderItem.builder()
                            .pedido(savedOrder)
                            .variante(variant)
                            .loteOrigen(batch)
                            .cantidad(cartItem.getCantidad())
                            .precioUnitario(cartItem.getPrecioUnitarioSnapshot())
                            .subtotal(cartItem.getPrecioUnitarioSnapshot()
                                    .multiply(BigDecimal.valueOf(cartItem.getCantidad())))
                            .descuentoItem(BigDecimal.ZERO)
                            .totalNeto(cartItem.getPrecioUnitarioSnapshot()
                                    .multiply(BigDecimal.valueOf(cartItem.getCantidad())))
                            .build();

                    // Crear movimiento de inventario (reserva)
                    InventoryMovement movement = InventoryMovement.builder()
                            .lote(batch)
                            .variante(variant)
                            .tipo(com.jhomilmotors.jhomilwebapp.enums.InventoryMovementType.RESERVA)
                            .cantidad(-cartItem.getCantidad())
                            .motivo("Reserva para pedido: " + codigo)
                            .usuario(usuario)
                            .fecha(LocalDateTime.now())
                            .build();

                    inventoryMovementRepository.save(movement);

                    // Actualizar cantidad disponible del lote
                    if (batch != null) {
                        batch.setCantidadDisponible(batch.getCantidadDisponible() - cartItem.getCantidad());
                        batchRepository.save(batch);
                    }

                    // Actualizar stock de variante
                    variant.setStock(variant.getStock() - cartItem.getCantidad());
                    productVariantRepository.save(variant);

                    return orderItem;
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        // Vaciar carrito
        cartRepository.delete(cart);

        return convertToDTO(orderRepository.save(savedOrder));
    }

    /**
     * Obtiene un pedido por ID
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        return convertToDTO(order);
    }

    /**
     * Obtiene pedidos del usuario
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByUser(Long usuarioId, Pageable pageable) {
        return orderRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Obtiene todos los pedidos (admin)
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Cambia el estado de un pedido
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String nuevoEstado) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        try {
            OrderStatus status = OrderStatus.fromValue(nuevoEstado);
            order.setEstado(status);
            Order updated = orderRepository.save(order);
            return convertToDTO(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pedido inválido: " + nuevoEstado);
        }
    }

    /**
     * Convierte entidad Order a DTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems() != null
                ? order.getItems().stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList())
                : List.of();

        List<OrderDTO.PromocionAplicadaDTO> promos = order.getPromocionesAplicadas() != null
                ? order.getPromocionesAplicadas().stream()
                .map(ap -> OrderDTO.PromocionAplicadaDTO.builder()
                        .nombreSnapshot(ap.getNombreSnapshot())
                        .valorDescuentoAplicado(ap.getValorDescuentoAplicado())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return OrderDTO.builder()
                .id(order.getId())
                .codigo(order.getCodigo())
                .usuarioId(order.getUsuario().getId())
                .usuarioNombre(order.getUsuario().getNombre())
                .fechaPedido(order.getFechaPedido())
                .estado(order.getEstado().getValue())
                .subtotal(order.getSubtotal())
                .descuento(order.getDescuento())
                .impuestos(order.getImpuestos())
                .costoEnvio(order.getCostoEnvio())
                .total(order.getTotal())
                .metodoPago(order.getMetodoPago())
                .direccionEnvio(order.getDireccionEnvio())
                .nota(order.getNota())
                .items(itemDTOs)
                .promocionesAplicadas(promos)
                .build();
    }

    /**
     * Convierte OrderItem a DTO
     */
    private OrderItemDTO convertOrderItemToDTO(OrderItem item) {
        ProductVariant variant = item.getVariante();
        Product product = variant.getProduct();

        return OrderItemDTO.builder()
                .id(item.getId())
                .pedidoId(item.getPedido().getId())
                .varianteId(variant.getId())
                .varianteSku(variant.getSku())
                .productoNombre(product.getNombre())
                .productoId(product.getId())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .descuentoItem(item.getDescuentoItem())
                .totalNeto(item.getTotalNeto())
                .loteOrigenId(item.getLoteOrigen() != null ? item.getLoteOrigen().getId() : null)
                .loteCodigoLote(item.getLoteOrigen() != null ? item.getLoteOrigen().getCodigoLote() : null)
                .build();
    }

}