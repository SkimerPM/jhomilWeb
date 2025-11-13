package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "core_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Column(name = "codigo", length = 100, nullable = false, unique = true)
    private String codigo;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @Column(name = "estado", length = 20, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.OrderStatusConverter.class)
    private OrderStatus estado = OrderStatus.PENDIENTE;

    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "descuento", precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "impuestos", precision = 12, scale = 2, nullable = false)
    private BigDecimal impuestos;

    @Column(name = "costo_envio", precision = 12, scale = 2, nullable = false)
    private BigDecimal costoEnvio;

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "direccion_envio", columnDefinition = "TEXT")
    private String direccionEnvio;

    @Column(name = "nota", columnDefinition = "TEXT")
    private String nota;

    // Relaciones inversas
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<OrderItem> items;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<AppliedPromotion> promocionesAplicadas;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Payment> pagos;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Shipment> envios;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Receipt> comprobantes;
}