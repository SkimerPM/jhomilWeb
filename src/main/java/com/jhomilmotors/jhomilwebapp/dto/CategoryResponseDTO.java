// com.jhomilmotors.jhomilwebapp.dto.CategoryResponseDTO.java
package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// 🚨 ¡IMPORTANTE! Este DTO lo usarás tanto para el método findAllCategories()
// como para el nuevo método de móvil.
public class CategoryResponseDTO {
    private Long id;
    private String nombre;

    // ⭐️ Nuevo campo para el móvil (será null si lo llamas desde findAllCategories())
    private String urlImagenCompleta;

    // Constructor que usa tu método actual (ID y Nombre)
    public CategoryResponseDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Constructor completo (ID, Nombre y URL de imagen)
    public CategoryResponseDTO(Long id, String nombre, String urlImagenCompleta) {
        this.id = id;
        this.nombre = nombre;
        this.urlImagenCompleta = urlImagenCompleta;
    }
}