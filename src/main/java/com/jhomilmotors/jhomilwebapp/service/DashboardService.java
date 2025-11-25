package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CustomerGrowthDto;
import com.jhomilmotors.jhomilwebapp.enums.RoleName;
import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DashboardService {

    private final UserRepository userRepository;

    public DashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CustomerGrowthDto getNewCustomersComparison() {
        LocalDate todayDate = LocalDate.now();
        LocalDate yesterdayDate = todayDate.minusDays(1);

        // Definir rangos de tiempo (Inicio y Fin del día)
        LocalDateTime todayStart = todayDate.atStartOfDay();
        LocalDateTime todayEnd = todayDate.atTime(LocalTime.MAX);

        LocalDateTime yesterdayStart = yesterdayDate.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterdayDate.atTime(LocalTime.MAX);

        // 1. Obtener conteo de HOY solo para CUSTOMER
        long todayCount = userRepository.countByRoleNameAndDateRange(
                RoleName.CUSTOMER,
                todayStart,
                todayEnd
        );

        // 2. Obtener conteo de AYER solo para CUSTOMER
        long yesterdayCount = userRepository.countByRoleNameAndDateRange(
                RoleName.CUSTOMER,
                yesterdayStart,
                yesterdayEnd
        );

        // 3. Calcular porcentaje
        double percentage = calculateGrowth(todayCount, yesterdayCount);

        return new CustomerGrowthDto(todayCount, yesterdayCount, percentage);
    }

    private double calculateGrowth(long today, long yesterday) {
        if (yesterday == 0) {
            return today > 0 ? 100.0 : 0.0;
        }

        double rawPercentage = ((double) (today - yesterday) / yesterday) * 100;

        // Redondear a 2 decimales
        BigDecimal bd = new BigDecimal(Double.toString(rawPercentage));
        bd = bd.setScale(2, RoundingMode.HALF_UP); // HALF_UP es el redondeo estándar

        return bd.doubleValue();
    }
}