package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByCompraId(Long compraId);

    List<Batch> findByProductoId(Long productoId);

    List<Batch> findByVarianteId(Long varianteId);

    List<Batch> findByProveedorId(Long proveedorId);

    List<Batch> findByCantidadDisponibleGreaterThan(Integer cantidad);

    List<Batch> findByFechaVencimientoBefore(LocalDate fecha);

    Optional<Batch> findByCodigoLote(String codigoLote);

    @Query("SELECT b FROM Batch b WHERE b.cantidadDisponible > 0 ORDER BY b.fechaIngreso ASC")
    List<Batch> findAvailableBatchesOrderByDate();

    @Query("SELECT b FROM Batch b WHERE b.cantidadDisponible > 0")
    List<Batch> findWithAvailableStock();

    // Búsquedas de vencimiento
    @Query("SELECT b FROM Batch b WHERE b.fechaVencimiento <= :fecha AND b.cantidadDisponible > 0")
    List<Batch> findExpiringBefore(@Param("fecha") LocalDate fecha);

    @Query("SELECT b FROM Batch b WHERE b.fechaVencimiento IS NULL OR b.fechaVencimiento > :fecha")
    List<Batch> findNonExpired(@Param("fecha") LocalDate fecha);

    // Búsquedas por almacén
    List<Batch> findByIdAlmacen(Integer idAlmacen);

}
