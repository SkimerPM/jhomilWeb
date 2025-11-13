package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long pedidoId;
    private String pedidoCodigo;
    private String metodo; // YAPE, PLIN, TRANSFERENCIA, CONTRAENTREGA, POS
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String estado; // PENDIENTE, CONFIRMADO, RECHAZADO
    private String comprobanteUrl;
    private String referenciaExterna;
    // Información del verificador (si fue confirmado por admin)
    private Long usuarioVerificadorId;
    private String usuarioVerificadorNombre;
    private LocalDateTime fechaValidacion;
}