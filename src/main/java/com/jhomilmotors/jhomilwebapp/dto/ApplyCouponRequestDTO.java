package com.jhomilmotors.jhomilwebapp.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyCouponRequestDTO {
    @NotBlank(message = "El código de cupón es requerido")
    private String codigo;
}
