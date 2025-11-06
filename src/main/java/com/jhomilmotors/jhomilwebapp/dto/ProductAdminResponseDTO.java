// Archivo: com/jhomilmotors/jhomilwebapp/dto/ProductAdminResponseDTO.java
package com.jhomilmotors.jhomilwebapp.dto;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;

    // SKU BASE: El campo que el panel de administración debe mostrar
    private String skuBase;

    private BigDecimal precioBase;
    private BigDecimal pesoKg;
    private BigDecimal volumenM3;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    // Información de relaciones
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;

    // LISTA ANIDADA: Para mostrar todas las variantes en el admin
    private List<ProductVariantAdminDTO> variantes;

    // Método estático para mapear la entidad Product completa (con variantes)
    public static ProductAdminResponseDTO fromEntity(Product entity) {

        // Mapea las variantes anidadas
        List<ProductVariantAdminDTO> variantDTOs = entity.getVariantes().stream()
                .map(ProductVariantAdminDTO::fromEntity)
                .collect(Collectors.toList());

        return new ProductAdminResponseDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getSkuBase(), // Aquí está el skuBase
                entity.getPrecioBase(),
                entity.getPesoKg(),
                entity.getVolumenM3(),
                entity.getActivo(),
                entity.getFechaCreacion(),

                // Mapeo de relaciones
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getNombre() : null,
                entity.getBrand() != null ? entity.getBrand().getId() : null,
                entity.getBrand() != null ? entity.getBrand().getNombre() : null,

                variantDTOs
        );
    }
}