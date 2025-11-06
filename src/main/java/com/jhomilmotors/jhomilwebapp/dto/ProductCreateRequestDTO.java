package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private String skuBase; // SKU único del producto

    @NotNull(message = "El precio base es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precioBase;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private Long marcaId; // Opcional según tu modelo Django

    // Datos para crear la VARIANTE inicial automáticamente
    @NotBlank(message = "El SKU de la variante es obligatorio")
    private String skuVariante;

    @NotNull(message = "El precio de la variante es obligatorio")
    @Positive(message = "El precio de variante debe ser positivo")
    private BigDecimal precioVariante;

    @NotNull(message = "El stock inicial es obligatorio")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero")
    private Integer stockInicial;

    private BigDecimal pesoKg;

    private BigDecimal volumenM3;

    // Indica si la imagen subida será la principal
    private Boolean imagenPrincipal = true;
}
