package com.jhomilmotors.jhomilwebapp.dto;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductListDTO {
    private Long id;
    private String nombre;
    private String skuBase;
    private BigDecimal precioBase;
    private String categoriaNombre;
    private String marcaNombre;
    private Boolean activo;
    private String imagenPrincipalUrl;

    public static AdminProductListDTO fromEntity(com.jhomilmotors.jhomilwebapp.entity.Product product) {
        // Obtiene la imagen principal: asume método getImagenes() disponible
        String imgUrl = null;
        if (product.getImagenes() != null && !product.getImagenes().isEmpty()) {
            imgUrl = product.getImagenes().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getEsPrincipal()))
                    .findFirst()
                    .map(com.jhomilmotors.jhomilwebapp.entity.Image::getUrl)
                    .orElse(product.getImagenes().get(0).getUrl());
        }
        return new AdminProductListDTO(
                product.getId(),
                product.getNombre(),
                product.getSkuBase(),
                product.getPrecioBase(),
                product.getCategory() != null ? product.getCategory().getNombre() : "N/A",
                product.getBrand() != null ? product.getBrand().getNombre() : "N/A",
                product.getActivo(),
                imgUrl
        );
    }
}