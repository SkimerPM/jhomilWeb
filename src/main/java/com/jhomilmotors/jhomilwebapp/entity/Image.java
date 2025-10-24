package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "core_imagen") // Nombre de tabla en la DB
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id") // Relación con el producto base
    private Product product;

    // NOTE: Podrías usar esta FK si las variantes tienen imágenes separadas
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "variante_id")
    // private ProductVariant variant;

    private String url;
    private Boolean esPrincipal; // Mapea a es_principal
    private Integer orden;
}