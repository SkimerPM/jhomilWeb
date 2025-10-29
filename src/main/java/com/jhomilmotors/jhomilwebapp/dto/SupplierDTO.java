package com.jhomilmotors.jhomilwebapp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {
    private Long id;
    private String nombre;
    private String ruc;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
}
