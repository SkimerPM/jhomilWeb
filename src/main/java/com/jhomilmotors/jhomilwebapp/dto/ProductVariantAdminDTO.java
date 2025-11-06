// Archivo: com/jhomilmotors/jhomilwebapp/dto/ProductVariantAdminDTO.java
package com.jhomilmotors.jhomilwebapp.dto;

import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor // Necesario para des-serialización
@AllArgsConstructor // Necesario para construcción simple
public class ProductVariantAdminDTO {

    private Long id;
    private String sku;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;
    private BigDecimal pesoKg;
    private LocalDateTime fechaCreacion;

    private List<ProductDetailsResponseDTO.ImagenResponse> imagenes;


    // Método de mapeo (para usarlo en el servicio)
    public static ProductVariantAdminDTO fromEntity(ProductVariant entity) {
        return new ProductVariantAdminDTO(
                entity.getId(),
                entity.getSku(),
                entity.getPrecio(),
                entity.getStock(),
                entity.getActivo(),
                entity.getPesoKg(),
                entity.getFechaCreacion(),
                Collections.emptyList()
        );
    }
}