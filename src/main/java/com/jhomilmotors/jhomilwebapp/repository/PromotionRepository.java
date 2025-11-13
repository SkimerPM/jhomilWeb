package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Promotion;
import com.jhomilmotors.jhomilwebapp.enums.DiscountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // === Métodos de búsqueda (Query Methods) ===

    // Buscar por código
    Optional<Promotion> findByCodigo(String codigo);

    // buscar promociones activas
    List<Promotion> findByActivoTrue();

    // buscar por tipo de descuento
    List<Promotion> findByTipoDescuento(DiscountType tipoDescuento);

    // Buscar promociones activas por tipo de descuento
    List<Promotion> findByActivoTrueAndTipoDescuento(DiscountType tipoDescuento);

    // buscar promociones vigentes segun fecha (requiere ambas fechas)
    List<Promotion> findByFechaInicioBeforeAndFechaFinAfter(LocalDateTime now1, LocalDateTime now2);

    // metodo para traer todas las promociones activas y vigentes (requiere ambas fechas)
    List<Promotion> findByActivoTrueAndFechaInicioBeforeAndFechaFinAfter(LocalDateTime start, LocalDateTime end);

    // === Métodos con @Query (Manejo de vigencia con fechaFin opcional) ===

    /**
     * Busca promociones activas y vigentes en un momento dado.
     * Considera que la fechaFin puede ser nula (vigencia indefinida).
     */
    @Query("SELECT p FROM Promotion p WHERE p.activo = true AND p.fechaInicio <= :ahora AND (p.fechaFin IS NULL OR p.fechaFin > :ahora)")
    List<Promotion> findPromotionsVigentes(@Param("ahora") LocalDateTime ahora);

    /**
     * Busca promociones activas, de un tipo específico y vigentes en un momento dado.
     * Considera que la fechaFin puede ser nula.
     */
    @Query("SELECT p FROM Promotion p WHERE p.activo = true AND p.tipoDescuento = :tipo AND p.fechaInicio <= :ahora AND (p.fechaFin IS NULL OR p.fechaFin > :ahora)")
    List<Promotion> findPromosByTipoAndVigent(@Param("tipo") DiscountType tipo, @Param("ahora") LocalDateTime ahora);

    // === Métodos de Paginación ===

    /**
     * Devuelve una página de todas las promociones.
     */
    Page<Promotion> findAll(Pageable pageable);

    /**
     * Devuelve una página de solo las promociones activas.
     */
    Page<Promotion> findByActivoTrue(Pageable pageable);

}