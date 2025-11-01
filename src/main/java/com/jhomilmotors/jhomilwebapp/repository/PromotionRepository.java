package com.jhomilmotors.jhomilwebapp.repository;


import com.jhomilmotors.jhomilwebapp.entity.Promotion;
import com.jhomilmotors.jhomilwebapp.enums.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    //buscar promociones activas
    List<Promotion> findByActivoTrue();
    //buscar por tipo de descuento
    List<Promotion> findByTipoDescuento(DiscountType tipoDescuento);
    //Buscar promociones activas por tipo de descuento
    List<Promotion> findByActivoTrueAndTipoDescuento(DiscountType tipoDescuento);
    //Buscar por código
    Optional<Promotion> findByCodigo(String codigo);
    //buscar proomciones vigentes segun fecha
    List<Promotion> findByFechaInicioBeforeAndFechaFinAfter(LocalDateTime now1, LocalDateTime now2);
    //metodo para traer todas las promociones activas y vigentes
    List<Promotion> findByActivoTrueAndFechaInicioBeforeAndFechaFinAfter(LocalDateTime start, LocalDateTime end);

}

