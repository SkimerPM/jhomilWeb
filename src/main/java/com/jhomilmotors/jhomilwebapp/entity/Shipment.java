package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Order pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private ShippingCompany empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_id")
    private City ciudad;

    @Column(name = "direccion", columnDefinition = "TEXT", nullable = false)
    private String direccion;

    @Column(name = "tracking", length = 255)
    private String tracking;

    @Column(name = "costo_envio", precision = 12, scale = 2, nullable = false)
    private BigDecimal costoEnvio;

    @Column(name = "estado_envio", length = 20, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.ShipmentStatusConverter.class)
    private ShipmentStatus estadoEnvio = ShipmentStatus.PENDIENTE;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;
}