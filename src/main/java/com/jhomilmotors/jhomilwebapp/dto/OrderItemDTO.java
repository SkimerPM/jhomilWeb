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
public class OrderItemDTO {
    private Long id;
    private Long pedidoId;
    // Datos del producto/variante
    private Long varianteId;
    private String varianteSku;
    private String productoNombre;
    private Long productoId;
    private String imagenUrl;

    // Cantidad y precios
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    // Trazabilidad de descuentos
    private BigDecimal descuentoItem;
    private String promocionAplicadaNombre;
    private BigDecimal totalNeto; // subtotal - descuentoItem

    // Información de lote (trazabilidad)
    private Long loteOrigenId;
    private String loteCodigoLote;
}