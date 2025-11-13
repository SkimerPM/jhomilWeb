package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Shipment;
import com.jhomilmotors.jhomilwebapp.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTracking(String tracking);
    List<Shipment> findByPedidoId(Long pedidoId);
    List<Shipment> findByEstadoEnvio(ShipmentStatus estadoEnvio);
    Page<Shipment> findByEstadoEnvio(ShipmentStatus estadoEnvio, Pageable pageable);
    List<Shipment> findByEmpresaId(Long empresaId);
    List<Shipment> findByCiudadId(Long ciudadId);
}
