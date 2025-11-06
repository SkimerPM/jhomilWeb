package com.jhomilmotors.jhomilwebapp.dto;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class LoginDTO {
    @Email(message = "El email debe tener el formato correcto")
    private String email;
    private String password;
}
