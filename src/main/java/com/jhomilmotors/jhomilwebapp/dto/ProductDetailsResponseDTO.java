package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductDetailsResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String skuBase;
    private BigDecimal precioBase;
    private Long brandId;
    private String marcaNombre;
    private Long categoryId;
    private String categoriaNombre;
    private Boolean activo;

    // Atributos del producto (características generales)
    private List<AtributoResponse> atributos;

    // Variantes disponibles (combinaciones de color, talla, etc.)
    private List<VarianteResponse> variantes;

    // Imágenes
    private List<ImagenResponse> imagenes;

    // Promociones activas
    private List<PromocionResponse> promociones;

    @Data
    @Builder
    public static class AtributoResponse {
        private Long id;
        private String nombre;
        private String codigo;
        private String tipo;
        private String unidad;
        private String valorTexto;
        private BigDecimal valorNumerico;
    }

    @Data
    @Builder
    public static class VarianteResponse {
        private Long id;
        private String sku;
        private BigDecimal precio;
        private Integer stock;
        private Boolean activo;
        private List<AtributoResponse> atributos; // Color: Rojo, Talla: M, etc.
        private List<ImagenResponse> imagenes;
    }

    @Data
    @Builder
    public static class ImagenResponse {
        private Long id;
        private String url;
        private Boolean esPrincipal;
        private Integer orden;
    }

    @Data
    @Builder
    public static class PromocionResponse {
        private String nombre;
        private String tipoDescuento;
        private BigDecimal valorDescuento;
        private BigDecimal precioConDescuento;
    }
}
