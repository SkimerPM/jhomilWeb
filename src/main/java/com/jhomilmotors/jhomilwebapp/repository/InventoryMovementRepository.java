package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.InventoryMovement;
import com.jhomilmotors.jhomilwebapp.enums.InventoryMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByVarianteId(Long varianteId);

    List<InventoryMovement> findByLoteId(Long loteId);

    List<InventoryMovement> findByTipo(InventoryMovementType tipo);

    List<InventoryMovement> findByUsuarioId(Long usuarioId);

    Page<InventoryMovement> findByVarianteId(Long varianteId, Pageable pageable);

    @Query("SELECT m FROM InventoryMovement m WHERE m.fecha BETWEEN :inicio AND :fin ORDER BY m.fecha DESC")
    List<InventoryMovement> findMovementsBetweenDates(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT m FROM InventoryMovement m WHERE m.variante.id = :varianteId ORDER BY m.fecha DESC")
    Page<InventoryMovement> findByVarianteIdOrderByFechaDesc(@Param("varianteId") Long varianteId, Pageable pageable);
}
