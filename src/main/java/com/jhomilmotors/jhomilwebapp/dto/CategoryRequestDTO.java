package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {
    private String nombre;
    private String slug;
    private String descripcion;
    private Long padreId; // Opcional, para subcategoría; null si es raíz
}
