package com.jhomilmotors.jhomilwebapp.dto.attribute.variant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaAttributeRequestDTO {
    private Long varianteId;
    private Long attributeId;
    private String valorText;
    private BigDecimal valorNum;
}
