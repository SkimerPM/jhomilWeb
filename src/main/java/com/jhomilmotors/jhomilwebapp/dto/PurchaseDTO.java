package com.jhomilmotors.jhomilwebapp.dto;


import com.jhomilmotors.jhomilwebapp.enums.PurchaseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDTO {
    private Long id;
    private Long proveedorId;
    private String proveedorNombre;
    private String codigo;
    private LocalDateTime fechaCompra;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private PurchaseStatus estado;
    private String nota;
    private List<PurchaseItemDTO> items;
}
