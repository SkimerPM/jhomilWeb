// Archivo: com/jhomilmotors/jhomilwebapp/dto/ProductVariantCreateRequestDTO.java

package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantCreateRequestDTO {

    // Son los campos que el ADMIN llenará en el formulario:

    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser un valor no negativo")
    private BigDecimal precio;

    @NotNull(message = "El stock inicial es obligatorio")
    @PositiveOrZero(message = "El stock debe ser un valor no negativo")
    private Integer stock;

    // Si tu entidad tiene peso, lo incluimos, ya que tu DTO de admin lo tiene.

    @PositiveOrZero(message = "El peso debe ser un valor no negativo")
    private BigDecimal pesoKg;

    // Nota: Aquí se omiten id, activo, fechaCreacion, ya que son del backend.
}