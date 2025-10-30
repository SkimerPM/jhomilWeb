package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByCompraId(Long compraId);

    List<Batch> findByProductoId(Long productoId);

    List<Batch> findByVarianteId(Long varianteId);

    List<Batch> findByProveedorId(Long proveedorId);

    List<Batch> findByCantidadDisponibleGreaterThan(Integer cantidad);

    List<Batch> findByFechaVencimientoBefore(LocalDate fecha);

    @Query("SELECT b FROM Batch b WHERE b.cantidadDisponible > 0 ORDER BY b.fechaIngreso ASC")
    List<Batch> findAvailableBatchesOrderByDate();
}
