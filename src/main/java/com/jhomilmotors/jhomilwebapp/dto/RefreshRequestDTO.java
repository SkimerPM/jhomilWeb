package com.jhomilmotors.jhomilwebapp.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDTO {
    private String refreshToken;

    public RefreshRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshRequestDTO() {
    }
}