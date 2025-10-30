package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSupplierDTO {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombre;

    private String ruc;
    private String contacto;
    private String telefono;

    @Email(message = "El email debe ser válido")
    private String email;

    private String direccion;
}
