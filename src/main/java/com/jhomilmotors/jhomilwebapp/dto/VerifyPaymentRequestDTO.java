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
public class VerifyPaymentRequestDTO {
    @NotNull(message = "El estado es requerido")
    private String estado; // CONFIRMADO, RECHAZADO
    private String motivo; // Motivo del rechazo si es necesario
}