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
public class ShipmentDTO {
    private Long id;
    private Long pedidoId;
    private String pedidoCodigo;
    // Información de envío
    private Long empresaId;
    private String empresaNombre;
    private Long ciudadId;
    private String ciudadNombre;
    private String direccion;
    private String tracking;
    private BigDecimal costoEnvio;
    private String estadoEnvio; // PENDIENTE, EN_TRANSITO, ENTREGADO, DEVUELTO

    // Fechas
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntregaEstimada;
    private LocalDateTime fechaEntregaReal;
}