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
public class ProductAttributeRequestDTO {
    private Long productId;
    private Long attributeId;
    private String valorText;
    private BigDecimal valorNum;
}
