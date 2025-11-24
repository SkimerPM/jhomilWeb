package com.jhomilmotors.jhomilwebapp.dto.brand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDTO {
    private Long id;
    private String nombre;
    private String imagenLogoURL;
}