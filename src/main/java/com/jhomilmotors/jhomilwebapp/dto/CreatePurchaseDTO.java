package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePurchaseDTO {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long proveedorId;

    private String codigo;

    private LocalDateTime fechaCompra;

    @NotNull(message = "El subtotal es obligatorio")
    private BigDecimal subtotal;

    @NotNull(message = "Los impuestos son obligatorios")
    private BigDecimal impuestos;

    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;

    private String nota;

    @NotEmpty(message = "Debe incluir al menos un item en la compra")
    @Valid
    private List<CreatePurchaseItemDTO> items;
}
