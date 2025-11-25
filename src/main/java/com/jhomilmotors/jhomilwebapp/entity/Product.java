package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "core_producto") // Nombre de tabla en la DB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id")
    private Brand brand;

    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "sku_base", length = 100, unique = true)
    private String skuBase;

    @Column(name = "precio_base", precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    @Column(name = "volumen_m3", precision = 10, scale = 6)
    private BigDecimal volumenM3;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones inversas
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ProductVariant> variantes;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Image> imagenes;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true) // orphanRemoval=true es CLAVE
    private List<ProductAttribute> atributos = new java.util.ArrayList<>();


    // Agregamos esto para que al borrar el producto, se borren sus apariciones en promociones
    @OneToMany(mappedBy = "producto", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<PromotionProduct> participacionesEnPromociones;

    // Opcional: Si quieres que también borre si el producto es el "productoGratis"
    @OneToMany(mappedBy = "productoGratis", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<PromotionProduct> participacionesComoGratis;
}