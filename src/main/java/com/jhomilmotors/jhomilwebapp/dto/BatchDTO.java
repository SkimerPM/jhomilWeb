package com.jhomilmotors.jhomilwebapp.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDTO {
    private Long id;
    private Long compraId;
    private Long proveedorId;
    private String proveedorNombre;
    private Long productoId;
    private String productoNombre;
    private Long varianteId;
    private String varianteSku;
    private String codigoLote;
    private String presentacion;
    private Integer unidadesPorPresentacion;
    private Integer cantidadInicial;
    private Integer cantidadDisponible;
    private BigDecimal costoTotal;
    private BigDecimal costoUnitario;
    private LocalDateTime fechaIngreso;
    private LocalDate fechaVencimiento;
    private Integer idAlmacen;
}
