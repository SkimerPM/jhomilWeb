package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenIndividualDTO {
    private Long id; // null para nueva imagen
    private String url;
    private Boolean esPrincipal;
    private Integer orden;
}
