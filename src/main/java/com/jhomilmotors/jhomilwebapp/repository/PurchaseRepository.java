package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Purchase;

import com.jhomilmotors.jhomilwebapp.enums.PurchaseStatus;
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
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByCodigo(String codigo);

    List<Purchase> findByProveedorId(Long proveedorId);

    List<Purchase> findByEstado(PurchaseStatus estado);

    List<Purchase> findByFechaCompraBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Purchase> findByProveedorIdAndEstado(Long proveedorId, PurchaseStatus estado);

    boolean existsByCodigo(String codigo);

    Page<Purchase> findByProveedorId(Long proveedorId, Pageable pageable);
    Page<Purchase> findByEstado(PurchaseStatus estado, Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE p.fechaCompra BETWEEN :inicio AND :fin ORDER BY p.fechaCompra DESC")
    List<Purchase> findPurchasesBetweenDates(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
