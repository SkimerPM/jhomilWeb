package com.jhomilmotors.jhomilwebapp.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequestDTO {
    @NotNull(message = "El pedido es requerido")
    private Long pedidoId;
    @NotNull(message = "El método de pago es requerido")
    private String metodo;

    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private String comprobanteUrl;
    private String referenciaExterna;
}
