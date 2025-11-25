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
import java.util.Collections;
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
     * Crea un pedido desde el carrito (LOGICA ORIGINAL CONSERVADA)
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
        // 1. 🧮 CALCULADORA
        // -------------------------------------------------------
        BigDecimal subtotalCalculado = cart.getItems().stream()
                .map(item -> item.getPrecioUnitarioSnapshot().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal impuestos = BigDecimal.ZERO;
        BigDecimal costoEnvio = BigDecimal.ZERO;

        // Total Final Real
        BigDecimal totalCalculado = subtotalCalculado.add(impuestos).add(costoEnvio).subtract(descuento);
        // -------------------------------------------------------

        // Crear pedido
        Order order = Order.builder()
                .usuario(usuario)
                .codigo(codigo)
                .fechaPedido(LocalDateTime.now())
                .estado(OrderStatus.PENDIENTE)
                .subtotal(subtotalCalculado)
                .descuento(descuento)
                .impuestos(impuestos)
                .costoEnvio(costoEnvio)
                .total(totalCalculado)
                .metodoPago(request.getMetodoPago())
                .direccionEnvio(request.getDireccionEnvio())
                .nota(request.getNota())
                .items(List.of()) // Inicializamos vacía
                .build();

        Order savedOrder = orderRepository.save(order);

        // Crear items y mover inventario
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

                    // Movimiento de inventario
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

                    if (batch != null) {
                        batch.setCantidadDisponible(batch.getCantidadDisponible() - cartItem.getCantidad());
                        batchRepository.save(batch);
                    }

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

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByUser(Long usuarioId, Pageable pageable) {
        return orderRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

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

    // ==========================================
    // MAPPERS ACTUALIZADOS (DTOs Completos y con getValue())
    // ==========================================

    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems() != null
                ? order.getItems().stream().map(this::convertOrderItemToDTO).collect(Collectors.toList())
                : Collections.emptyList();

        List<OrderDTO.PromocionAplicadaDTO> promos = order.getPromocionesAplicadas() != null
                ? order.getPromocionesAplicadas().stream().map(this::convertPromoToDTO).collect(Collectors.toList())
                : Collections.emptyList();

        List<OrderDTO.PagoResumenDTO> pagos = order.getPagos() != null
                ? order.getPagos().stream().map(this::convertPagoToDTO).collect(Collectors.toList())
                : Collections.emptyList();

        OrderDTO.EnvioResumenDTO envioDTO = null;
        if (order.getEnvios() != null && !order.getEnvios().isEmpty()) {
            Shipment ultimoEnvio = order.getEnvios().get(order.getEnvios().size() - 1);
            envioDTO = convertEnvioToDTO(ultimoEnvio);
        }

        // CORRECCIÓN: Usamos getValue() aquí en order.getEstado() también
        String estadoStr = (order.getEstado() != null) ? order.getEstado().getValue() : "Desconocido";

        return OrderDTO.builder()
                .id(order.getId())
                .codigo(order.getCodigo())
                .usuarioId(order.getUsuario().getId())
                .usuarioNombre(order.getUsuario().getNombre() + " " + order.getUsuario().getApellido())
                .fechaPedido(order.getFechaPedido())
                .estado(estadoStr)
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
                .pagos(pagos)
                .envio(envioDTO)
                .build();
    }

    private OrderItemDTO convertOrderItemToDTO(OrderItem item) {
        ProductVariant variant = item.getVariante();
        String imgUrl = "/images/placeholder.png";
        String productoNombre = "Producto desconocido";
        Long productoId = null;

        if (variant != null) {
            Product product = variant.getProduct();
            if (product != null) {
                productoNombre = product.getNombre();
                productoId = product.getId();
                if (variant.getImagenes() != null && !variant.getImagenes().isEmpty()) {
                    imgUrl = variant.getImagenes().get(0).getUrl();
                } else if (product.getImagenes() != null && !product.getImagenes().isEmpty()) {
                    imgUrl = product.getImagenes().get(0).getUrl();
                }
            }
        }

        return OrderItemDTO.builder()
                .id(item.getId())
                .pedidoId(item.getPedido().getId())
                .varianteId(variant != null ? variant.getId() : null)
                .varianteSku(variant != null ? variant.getSku() : "N/A")
                .productoNombre(productoNombre)
                .productoId(productoId)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .descuentoItem(item.getDescuentoItem())
                .totalNeto(item.getTotalNeto())
                .loteOrigenId(item.getLoteOrigen() != null ? item.getLoteOrigen().getId() : null)
                .loteCodigoLote(item.getLoteOrigen() != null ? item.getLoteOrigen().getCodigoLote() : null)
                .imagenUrl(imgUrl)
                .build();
    }

    private OrderDTO.PromocionAplicadaDTO convertPromoToDTO(AppliedPromotion ap) {
        return OrderDTO.PromocionAplicadaDTO.builder()
                .nombreSnapshot(ap.getNombreSnapshot())
                .valorDescuentoAplicado(ap.getValorDescuentoAplicado())
                .build();
    }

    private OrderDTO.PagoResumenDTO convertPagoToDTO(Payment pago) {
        // CORRECCIÓN: Usamos getValue() para método y estado de pago
        // Agregamos chequeo de nulos por seguridad
        String metodoStr = (pago.getMetodo() != null) ? pago.getMetodo().getValue() : null;
        String estadoStr = (pago.getEstado() != null) ? pago.getEstado().getValue() : null;

        return OrderDTO.PagoResumenDTO.builder()
                .id(pago.getId())
                .metodo(metodoStr)
                .monto(pago.getMonto())
                .estado(estadoStr)
                .fechaPago(pago.getFechaPago())
                .build();
    }

    private OrderDTO.EnvioResumenDTO convertEnvioToDTO(Shipment envio) {
        String empresa = (envio.getEmpresa() != null) ? envio.getEmpresa().getNombre() : "Desconocido";
        // CORRECCIÓN: Usamos getValue() para estado de envío
        String estadoEnvioStr = (envio.getEstadoEnvio() != null) ? envio.getEstadoEnvio().getValue() : null;

        return OrderDTO.EnvioResumenDTO.builder()
                .id(envio.getId())
                .empresaNombre(empresa)
                .tracking(envio.getTracking())
                .estado(estadoEnvioStr)
                .fechaEntregaEstimada(envio.getFechaEntregaEstimada())
                .build();
    }
}