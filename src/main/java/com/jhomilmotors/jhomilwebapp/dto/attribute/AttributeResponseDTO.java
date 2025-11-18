package com.jhomilmotors.jhomilwebapp.dto.attribute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeResponseDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String tipo;
    private String unidad;
    private Boolean esVariacion;
    private Integer ordenVisual;
}
