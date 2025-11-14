package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryAdminDTO {
    private Long id;
    private String nombre;
    private String slug;
    private String descripcion;
    private Long padreId;
    private String padreNombre;
    private List<CategoryAdminDTO> subcategorias;
}
