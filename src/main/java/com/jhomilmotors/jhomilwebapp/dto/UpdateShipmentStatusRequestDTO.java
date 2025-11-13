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
public class UpdateShipmentStatusRequestDTO {
    @NotNull(message = "El estado es requerido")
    private String estadoEnvio; // PENDIENTE, EN_TRANSITO, ENTREGADO, DEVUELTO
    private String tracking;
}