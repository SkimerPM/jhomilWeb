package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductCatalogResponseDTO {
    private Long id; // ID del Producto base
    private String nombre;
    private String descripcion;

    // Datos clave de la variante principal
    private BigDecimal precio;
    private Integer stockDisponible;

    // Imagen
    private String imagenUrl;

    // Para filtros y visualización
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;
}
