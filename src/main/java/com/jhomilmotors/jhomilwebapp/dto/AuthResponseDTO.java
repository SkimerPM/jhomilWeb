package com.jhomilmotors.jhomilwebapp.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    private String accessToken;
    private String email;
    private String role;
    private String refreshToken; //para móvil

    public AuthResponseDTO(String accessToken, String email, String role) {
        this.accessToken = accessToken;
        this.email = email;
        this.role = role;
    }

    public AuthResponseDTO(String accessToken, String email, String role, String refreshToken) {
        this.accessToken = accessToken;
        this.email = email;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    public AuthResponseDTO() {}
}
