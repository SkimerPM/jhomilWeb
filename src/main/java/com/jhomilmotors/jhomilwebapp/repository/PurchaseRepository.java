package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Purchase;
import com.jhomilmotors.jhomilwebapp.entity.Purchase.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
