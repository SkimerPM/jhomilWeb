package com.jhomilmotors.jhomilwebapp.dto.attribute.normal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeResponseDTO {
    private Long id;
    private Long productId;
    private Long attributeId;
    private String attributeName;
    private String attributeType;
    private String attributeUnidad;
    private String valorText;
    private BigDecimal valorNum;
}