package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_producto") // Nombre de tabla en la DB
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id")
    private Brand brand;

    @Column(name="nombre") // <-- ¡aunque no tenga guion bajo!
    private String nombre;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name="sku_base")
    private String skuBase;

    @Column(name = "precio_base")
    private BigDecimal precioBase;

    @Column(name="activo")
    private Boolean activo;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;
}