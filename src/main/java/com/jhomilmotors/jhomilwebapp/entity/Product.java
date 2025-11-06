package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    // ⬇️ AGREGAR ESTOS CAMPOS QUE ESTÁN EN TU MODELO DJANGO
    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    @Column(name = "volumen_m3", precision = 10, scale = 6)
    private BigDecimal volumenM3;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Colección de Variantes: Usa 'variantes' como en el related_name de Django
    // Usamos FetchType.LAZY, pero lo cargaremos explícitamente en la Query (Ver Repositorio)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductVariant> variantes;
}