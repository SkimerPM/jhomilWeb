package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_productovariante") // ¡Ajusta este nombre de tabla si es diferente!
@Data
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación al producto base (Product.java)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Product product;

    @Column(name="sku")
    private String sku;
    private BigDecimal precio; // << El precio de venta real
    private Integer stock;
    private Boolean activo;

    // ⬇️ AGREGAR ESTE CAMPO
    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    // ⬇️ AGREGAR ESTOS CAMPOS DE FECHAS
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ... otros campos como peso_kg, fechas ...
}