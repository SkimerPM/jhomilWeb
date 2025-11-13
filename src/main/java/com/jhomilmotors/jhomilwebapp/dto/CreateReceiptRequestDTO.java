package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReceiptRequestDTO {
    @NotNull(message = "El pedido es requerido")
    private Long pedidoId;
    @NotNull(message = "El tipo de comprobante es requerido")
    private String tipo; // BOLETA, FACTURA

    private String numero;
}