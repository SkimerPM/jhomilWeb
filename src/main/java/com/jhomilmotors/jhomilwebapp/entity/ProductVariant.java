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

    private String sku;
    private BigDecimal precio; // << El precio de venta real
    private Integer stock;
    private Boolean activo;

    // ... otros campos como peso_kg, fechas ...
}