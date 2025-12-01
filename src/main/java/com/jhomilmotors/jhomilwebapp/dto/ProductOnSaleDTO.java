package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductOnSaleDTO {
    // IDs de navegación
    private Long promotionProductId;
    private Long productId;
    private Long variantId; // Será null si es oferta de producto base

    // Datos visuales
    private String productName;
    private String sku; // Útil para mostrar referencias
    private String imageUrl;

    // Datos de la Promoción
    private String promotionLabel; // Ej: "Descuento Verano", "2x1"
    private String discountType;   // "PORCENTAJE", "MONTO_FIJO", "DOS_POR_UNO"

    // Datos Financieros
    private BigDecimal originalPrice; // Precio tachado
    private BigDecimal discountAmount; // Cuánto se ahorra (dinero)
    private BigDecimal finalPrice;    // Precio grande final
}