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
public class VaAttributeResponseDTO {
    private Long id;
    private Long varianteId;
    private Long atributoId;
    private String atributoNombre;
    private String atributoTipo;
    private String atributoUnidad;
    private String valorText;
    private BigDecimal valorNum;
}
