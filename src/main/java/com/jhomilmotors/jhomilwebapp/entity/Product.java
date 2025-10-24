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

    // Relaciones (Foreign Keys de Django)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id") // Nombre de la columna FK
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id") // Nombre de la columna FK
    private Brand brand;

    // Campos directos
    private String nombre;
    private String descripcion;
    private String skuBase; // Mapea a sku_base
    private BigDecimal precioBase; // Mapea a precio_base
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    // ... otros campos como peso_kg, volumen_m3 si son necesarios en otras partes.
}