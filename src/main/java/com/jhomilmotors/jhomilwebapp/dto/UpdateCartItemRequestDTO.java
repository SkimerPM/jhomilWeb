package com.jhomilmotors.jhomilwebapp.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequestDTO {
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}