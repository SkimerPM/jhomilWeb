package com.jhomilmotors.jhomilwebapp.dto;

import com.jhomilmotors.jhomilwebapp.entity.Purchase.PurchaseStatus;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePurchaseDTO {
    private String codigo;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private PurchaseStatus estado;
    private String nota;
}
