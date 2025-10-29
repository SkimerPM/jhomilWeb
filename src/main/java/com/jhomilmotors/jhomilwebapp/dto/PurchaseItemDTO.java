package com.jhomilmotors.jhomilwebapp.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long varianteId;
    private String varianteSku;
    private String presentacion;
    private Integer unidadesPorPresentacion;
    private Integer cantidadPresentaciones;
    private Integer cantidadUnidades;
    private BigDecimal precioUnitarioPresentacion;
    private BigDecimal precioUnitarioUnidad;
    private BigDecimal subtotal;
}
