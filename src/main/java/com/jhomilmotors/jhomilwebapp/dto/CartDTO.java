package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private Long usuarioId;
    private String sessionId; // Para carritos anónimos
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Boolean activo;
    // Items del carrito
    private List<CartItemDTO> items;

    // Totales calculados
    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal impuestos;
    private BigDecimal costoEnvio;
    private BigDecimal total;

    // Promociones aplicadas
    private List<CuponAplicadoDTO> cuponesAplicados;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuponAplicadoDTO {
        private String codigo;
        private String nombre;
        private BigDecimal descuentoAplicado;
    }
}