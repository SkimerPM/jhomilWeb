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
public class ProductUpdateRequestDTO {
    // Campos del producto principal (todos editables salvo id y fechaCreacion)
    private String nombre;
    private String descripcion;
    private String skuBase;
    private BigDecimal pesoKg;
    private BigDecimal precioBase;
    private BigDecimal volumenM3;
    private Long categoryId;
    private Long brandId;
    private Boolean activo;
    private List<ImagenRequest> imagenes; // Lista completa. Borra existenes y crea nuevas.
    private List<VarianteUpdateRequest> variantes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VarianteUpdateRequest {
        private Long id; // Si null, es variante nueva. Si existe, se actualiza o elimina si está en la lista de variantes a eliminar.
        private String sku;
        private BigDecimal precio;
        private Integer stock;
        private Boolean activo;
        private BigDecimal pesoKg;
        private Boolean eliminar; // Si true, se elimina la variante.
        private List<ImagenRequest> imagenes;
        private List<AtributoVarianteRequest> atributos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtributoVarianteRequest {
        private Long atributoId; // El ID del atributo (ej: 1 para Color)
        private String valor;    // El valor seleccionado (ej: "Rojo", "XL")
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImagenRequest {
        private Long id;
        private String url;
        private Boolean esPrincipal;
        private Integer orden;
    }
}
