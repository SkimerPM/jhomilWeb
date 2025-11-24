package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {
    private String orderCode;   // Pedido.codigo
    private BigDecimal amount;      // Pedido.total
    private String title;       // Producto o descripción general


}