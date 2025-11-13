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
public class ReceiptDTO {
    private Long id;
    private Long pedidoId;
    private String pedidoCodigo;
    private String tipo; // BOLETA, FACTURA
    private String numero;
    private LocalDateTime fechaEmision;
    private BigDecimal montoTotal;
    private BigDecimal impuesto;
    private String pdfUrl;
    private String estado; // EMITIDA, ANULADA
}