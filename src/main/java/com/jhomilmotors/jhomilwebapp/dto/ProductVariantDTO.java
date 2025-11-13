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
public class ProductVariantDTO {
    private Long id;
    private String sku;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;
    private BigDecimal pesoKg;
    // Información del producto
    private Long productoId;
    private String productoNombre;

    // Atributos específicos (Color: Rojo, Talla: M, etc.)
    private List<VarianteAtributoDTO> atributos;

    // Imágenes específicas de la variante
    private List<ImagenResponse> imagenes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VarianteAtributoDTO {
        private Long id;
        private String atributoNombre;
        private String atributoCodigo;
        private String atributoTipo;
        private String valorTexto;
        private BigDecimal valorNum;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImagenResponse {
        private Long id;
        private String url;
        private Boolean esPrincipal;
        private Integer orden;
    }
}
