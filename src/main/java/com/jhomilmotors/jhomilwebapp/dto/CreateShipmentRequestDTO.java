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
public class CreateShipmentRequestDTO {
    @NotNull(message = "El pedido es requerido")
    private Long pedidoId;
    @NotNull(message = "La empresa de envío es requerida")
    private Long empresaId;

    @NotNull(message = "La ciudad es requerida")
    private Long ciudadId;

    private String tracking;
}