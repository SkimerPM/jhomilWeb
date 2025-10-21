// LogoutRequestDTO.java
package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LogoutRequestDTO {
    private String refreshToken;

    public LogoutRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LogoutRequestDTO() {
    }
}