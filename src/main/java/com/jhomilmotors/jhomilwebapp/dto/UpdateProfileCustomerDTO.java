package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileCustomerDTO {
    @NotBlank(message = "El nombre no puede estar vacío.")
    String name;

    @NotBlank(message = "El apellido no puede estar vacío.")
    String lastname;
    @Email(message = "El formato del email es inválido.")
    @NotBlank(message = "El email no puede estar vacío.")
    String email;
    String phoneNumber;
    String address;
}
