package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {
    @NotBlank(message = "La dirección de envío es requerida")
    private String direccionEnvio;
    private String nota;
    private String metodoPago;
}