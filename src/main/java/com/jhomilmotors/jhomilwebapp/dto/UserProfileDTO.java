package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.Email;

public record UserProfileDTO (
        String nombre,
        @Email(message = "El email debe ser válido")
        String email,
        String rol
){}
