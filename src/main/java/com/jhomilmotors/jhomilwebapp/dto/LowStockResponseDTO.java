package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockResponseDTO {
    private Long varianteId;
    private String productoNombre;
    private String sku;
    private Integer stock;
    private String imagenUrl;
    private String marca;
}