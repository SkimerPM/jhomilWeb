package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CreateShipmentRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.ShipmentDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateShipmentStatusRequestDTO;
import com.jhomilmotors.jhomilwebapp.entity.Order;
import com.jhomilmotors.jhomilwebapp.entity.Shipment;
import com.jhomilmotors.jhomilwebapp.entity.ShippingCompany;
import com.jhomilmotors.jhomilwebapp.entity.City;
import com.jhomilmotors.jhomilwebapp.enums.ShipmentStatus;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final ShippingCompanyRepository shippingCompanyRepository;
    private final CityRepository cityRepository;

    /**
     * Crea un envío para una orden
     */
    @Transactional
    public ShipmentDTO createShipment(CreateShipmentRequestDTO request) {
        Order order = orderRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        ShippingCompany empresa = shippingCompanyRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa de envío no encontrada"));

        City ciudad = cityRepository.findById(request.getCiudadId())
                .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada"));

        Shipment shipment = Shipment.builder()
                .pedido(order)
                .empresa(empresa)
                .ciudad(ciudad)
                .direccion(order.getDireccionEnvio())
                .tracking(request.getTracking())
                .costoEnvio(order.getCostoEnvio())
                .estadoEnvio(ShipmentStatus.PENDIENTE)
                .fechaEntregaEstimada(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .build();

        Shipment saved = shipmentRepository.save(shipment);
        return convertToDTO(saved);
    }

    /**
     * Obtiene envío de una orden
     */
    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentByOrder(Long orderId) {
        Shipment shipment = shipmentRepository.findByPedidoId(orderId).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado para la orden"));
        return convertToDTO(shipment);
    }

    /**
     * Actualiza el estado del envío
     */
    @Transactional
    public ShipmentDTO updateShipmentStatus(Long shipmentId, UpdateShipmentStatusRequestDTO request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));

        try {
            ShipmentStatus status = ShipmentStatus.fromValue(request.getEstadoEnvio());
            shipment.setEstadoEnvio(status);

            if (request.getTracking() != null) {
                shipment.setTracking(request.getTracking());
            }

            if (status == ShipmentStatus.ENTREGADO) {
                shipment.setFechaEntregaReal(LocalDateTime.now());
            }

            Shipment updated = shipmentRepository.save(shipment);
            return convertToDTO(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de envío inválido: " + request.getEstadoEnvio());
        }
    }

    /**
     * Obtiene todos los envíos (admin)
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Obtiene envíos por estado
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> getShipmentsByStatus(String estado, Pageable pageable) {
        try {
            ShipmentStatus status = ShipmentStatus.fromValue(estado);
            return shipmentRepository.findByEstadoEnvio(status, pageable)
                    .map(this::convertToDTO);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de envío inválido: " + estado);
        }
    }

    /**
     * Convierte Shipment a DTO
     */
    private ShipmentDTO convertToDTO(Shipment shipment) {
        return ShipmentDTO.builder()
                .id(shipment.getId())
                .pedidoId(shipment.getPedido().getId())
                .pedidoCodigo(shipment.getPedido().getCodigo())
                .empresaId(shipment.getEmpresa() != null ? shipment.getEmpresa().getId() : null)
                .empresaNombre(shipment.getEmpresa() != null ? shipment.getEmpresa().getNombre() : null)
                .ciudadId(shipment.getCiudad() != null ? shipment.getCiudad().getId() : null)
                .ciudadNombre(shipment.getCiudad() != null ? shipment.getCiudad().getNombre() : null)
                .direccion(shipment.getDireccion())
                .tracking(shipment.getTracking())
                .costoEnvio(shipment.getCostoEnvio())
                .estadoEnvio(shipment.getEstadoEnvio().getValue())
                .fechaEnvio(shipment.getFechaEnvio())
                .fechaEntregaEstimada(shipment.getFechaEntregaEstimada())
                .fechaEntregaReal(shipment.getFechaEntregaReal())
                .build();
    }

}