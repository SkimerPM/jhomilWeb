package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleProductRequestDTO {
    // Datos Generales
    private String nombre;
    private String descripcion;
    private String sku; // Se usará como skuBase

    // Datos Numéricos
    private BigDecimal precio;
    private Integer stock;

    // Dimensiones
    private BigDecimal pesoKg;
    private BigDecimal volumenM3;

    // Relaciones
    private Long categoryId;
    private Long brandId;

    // --- NUEVO: Lista de Atributos (Especificaciones del producto) ---
    private List<SimpleAtributoRequest> atributos;

    // Imágenes
    private List<SimpleImagenRequest> imagenes;

    // --- Subclases para estructura JSON ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleImagenRequest {
        private String url;
        private Boolean esPrincipal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleAtributoRequest {
        private Long atributoId; // ID del atributo (ej: ID de "Material")
        private String valor;    // Valor (ej: "Acero Inoxidable")
    }
}