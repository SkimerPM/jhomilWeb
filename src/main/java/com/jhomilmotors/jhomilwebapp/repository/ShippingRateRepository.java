package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {
    List<ShippingRate> findByCiudadId(Long ciudadId);

    List<ShippingRate> findByEmpresaId(Long empresaId);

    Optional<ShippingRate> findByCiudadIdAndEmpresaId(Long ciudadId, Long empresaId);

    List<ShippingRate> findByActivoTrue();

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.ciudad.id = :ciudadId AND sr.empresa.id = :empresaId AND :peso BETWEEN sr.pesoMinKg AND sr.pesoMaxKg")
    Optional<ShippingRate> findByPesoRango(@Param("ciudadId") Long ciudadId, @Param("empresaId") Long empresaId, @Param("peso") BigDecimal peso);
}
