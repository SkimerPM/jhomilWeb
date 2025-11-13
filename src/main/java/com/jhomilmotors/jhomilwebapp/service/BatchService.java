package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.Batch;
import com.jhomilmotors.jhomilwebapp.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final BatchRepository batchRepository;

    public List<Batch> getAll() {
        return batchRepository.findAll();
    }

    public List<Batch> getByCompraId(Long compraId) {
        return batchRepository.findByCompraId(compraId);
    }

    public List<Batch> getByProductoId(Long productoId) {
        return batchRepository.findByProductoId(productoId);
    }

    public List<Batch> getByVarianteId(Long varianteId) {
        return batchRepository.findByVarianteId(varianteId);
    }

    public List<Batch> getByProveedorId(Long proveedorId) {
        return batchRepository.findByProveedorId(proveedorId);
    }

    public List<Batch> getWithMoreThanXAvailable(int cantidad) {
        return batchRepository.findByCantidadDisponibleGreaterThan(cantidad);
    }

    public List<Batch> getByFechaVencimientoBefore(LocalDate fecha) {
        return batchRepository.findByFechaVencimientoBefore(fecha);
    }

    public Optional<Batch> getByCodigoLote(String codigoLote) {
        return batchRepository.findByCodigoLote(codigoLote);
    }

    public List<Batch> getAvailableOrderByDate() {
        return batchRepository.findAvailableBatchesOrderByDate();
    }

    public List<Batch> getWithAvailableStock() {
        return batchRepository.findWithAvailableStock();
    }

    public List<Batch> getExpiringBefore(LocalDate fecha) {
        return batchRepository.findExpiringBefore(fecha);
    }

    public List<Batch> getNonExpired(LocalDate fecha) {
        return batchRepository.findNonExpired(fecha);
    }

    public List<Batch> getByAlmacen(Integer idAlmacen) {
        return batchRepository.findByIdAlmacen(idAlmacen);
    }
}

