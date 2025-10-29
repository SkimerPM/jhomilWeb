package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePurchaseItemDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private Long varianteId;

    private String presentacion;

    @NotNull(message = "Las unidades por presentación son obligatorias")
    @Min(value = 1, message = "Debe ser al menos 1")
    private Integer unidadesPorPresentacion;

    @NotNull(message = "La cantidad de presentaciones es obligatoria")
    @Min(value = 1, message = "Debe ser al menos 1")
    private Integer cantidadPresentaciones;

    @NotNull(message = "La cantidad de unidades es obligatoria")
    @Min(value = 1, message = "Debe ser al menos 1")
    private Integer cantidadUnidades;

    @NotNull(message = "El precio unitario por presentación es obligatorio")
    private BigDecimal precioUnitarioPresentacion;

    @NotNull(message = "El precio unitario por unidad es obligatorio")
    private BigDecimal precioUnitarioUnidad;

    @NotNull(message = "El subtotal es obligatorio")
    private BigDecimal subtotal;
}
