package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long carritoId;
    // Datos de la variante
    private Long varianteId;
    private String varianteSku;
    private String productoNombre;
    private String imagenUrl; // Imagen principal de la variante

    // Cantidad y precio
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal; // cantidad * precioUnitario

    // Información de stock
    private Integer stockDisponible;
}