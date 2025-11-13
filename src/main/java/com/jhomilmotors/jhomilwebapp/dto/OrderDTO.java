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
public class OrderDTO {
    private Long id;
    private String codigo;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaPedido;
    private String estado; // PENDIENTE, PAGADO, PREPARANDO, ENVIADO, ENTREGADO, CANCELADO
    // Totales
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal impuestos;
    private BigDecimal costoEnvio;
    private BigDecimal total;

    // Datos de envío
    private String metodoPago;
    private String direccionEnvio;
    private String nota;

    // Items del pedido
    private List<OrderItemDTO> items;

    // Promociones aplicadas
    private List<PromocionAplicadaDTO> promocionesAplicadas;

    // Información de pago
    private List<PagoResumenDTO> pagos;

    // Información de envío
    private EnvioResumenDTO envio;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromocionAplicadaDTO {
        private String nombreSnapshot;
        private BigDecimal valorDescuentoAplicado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagoResumenDTO {
        private Long id;
        private String metodo;
        private BigDecimal monto;
        private String estado;
        private LocalDateTime fechaPago;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvioResumenDTO {
        private Long id;
        private String empresaNombre;
        private String tracking;
        private String estado;
        private LocalDateTime fechaEntregaEstimada;
    }
}