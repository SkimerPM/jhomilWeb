package com.jhomilmotors.jhomilwebapp.dto.brand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequestDTO {
    private String nombre;
    private String imagenLogoURL;
}
