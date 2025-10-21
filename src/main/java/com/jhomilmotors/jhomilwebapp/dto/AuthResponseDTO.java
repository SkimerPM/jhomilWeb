package com.jhomilmotors.jhomilwebapp.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String role;

    public AuthResponseDTO(String accessToken, String refreshToken, String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
    }

    public AuthResponseDTO() {
    }
}