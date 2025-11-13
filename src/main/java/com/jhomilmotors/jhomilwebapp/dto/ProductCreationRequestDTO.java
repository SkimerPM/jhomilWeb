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
public class ProductCreationRequestDTO {
    // Campos del producto principal
    private String nombre;
    private String descripcion;
    private String skuBase;
    private BigDecimal precioBase;
    private BigDecimal pesoKg;
    private BigDecimal volumenM3;
    private Long categoryId;
    private Long brandId;
    private Boolean activo;
    private List<VarianteRequest> variantes;      // Variantes del producto
    private List<ImagenRequest> imagenes;         // Imágenes principales del producto

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VarianteRequest {
        // Campos SOLO de la variante, que no deben confundirse con el producto principal
        private String sku;
        private BigDecimal precio;
        private Integer stock;
        private Boolean activo;
        private BigDecimal pesoKg;
        private List<VarianteAtributoRequest> atributos;
        private List<ImagenRequest> imagenes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VarianteAtributoRequest {
        private Long atributoId;
        private String valorTexto;
        private BigDecimal valorNum;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImagenRequest {
        private String url;
        private Boolean esPrincipal;
        private Integer orden;
    }
}
