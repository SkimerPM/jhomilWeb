package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "core_compraitem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private Purchase compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    private ProductVariant variante;

    @Column(length = 100)
    private String presentacion;

    @Column(name = "unidades_por_presentacion", nullable = false)
    private Integer unidadesPorPresentacion;

    @Column(name = "cantidad_presentaciones", nullable = false)
    private Integer cantidadPresentaciones;

    @Column(name = "cantidad_unidades", nullable = false)
    private Integer cantidadUnidades;

    @Column(name = "precio_unitario_presentacion", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioPresentacion;

    @Column(name = "precio_unitario_unidad", nullable = false, precision = 12, scale = 4)
    private BigDecimal precioUnitarioUnidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
