package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGrowthDto {
    private long todayCount;        // Cantidad exacta hoy
    private long yesterdayCount;    // Cantidad exacta ayer
    private double growthPercentage; // Porcentaje de diferencia
}