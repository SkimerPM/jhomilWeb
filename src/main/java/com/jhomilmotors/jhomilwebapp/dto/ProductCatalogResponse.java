package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data // Solo necesitamos Getters y Setters
// 🛑 ¡ELIMINAR @Builder!
public class ProductCatalogResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private Long stockTotal;
    private String imagenUrl;
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;
    private String sku; // Campo 11

    private List<ImageResponseDTO> imagenes; // Campo 12

    /**
     * CONSTRUCTOR MANUAL DE 11 ARGUMENTOS (Para HQL/JPQL).
     */
    public ProductCatalogResponse(
            Long id,
            String nombre,
            String descripcion,
            BigDecimal precioBase,
            Long stockTotal,
            String imagenUrl,
            Long categoriaId,
            String categoriaNombre,
            Long marcaId,
            String marcaNombre,
            String sku
    ) {
        // ... (Asignación de los 11 campos)
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.stockTotal = stockTotal;
        this.imagenUrl = imagenUrl;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.marcaId = marcaId;
        this.marcaNombre = marcaNombre;
        this.sku = sku;

        this.imagenes = null;
    }

    /**
     * CONSTRUCTOR VACÍO (Recomendado para Spring/Jackson).
     */
    public ProductCatalogResponse() {
    }
}
