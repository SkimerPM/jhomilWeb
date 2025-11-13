package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CreatePaymentRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.PaymentDTO;
import com.jhomilmotors.jhomilwebapp.dto.VerifyPaymentRequestDTO;
import com.jhomilmotors.jhomilwebapp.entity.Order;
import com.jhomilmotors.jhomilwebapp.entity.Payment;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.enums.OrderStatus;
import com.jhomilmotors.jhomilwebapp.enums.PaymentMethod;
import com.jhomilmotors.jhomilwebapp.enums.PaymentStatus;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.OrderRepository;
import com.jhomilmotors.jhomilwebapp.repository.PaymentRepository;
import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * Registra un nuevo pago
     */
    @Transactional
    public PaymentDTO createPayment(CreatePaymentRequestDTO request) {
        Order order = orderRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        // Validar que el monto sea igual o mayor al total del pedido
        if (request.getMonto().compareTo(order.getTotal()) < 0) {
            throw new IllegalArgumentException("El monto del pago es menor al total del pedido");
        }

        try {
            PaymentMethod method = PaymentMethod.fromValue(request.getMetodo());

            Payment payment = Payment.builder()
                    .pedido(order)
                    .metodo(method)
                    .monto(request.getMonto())
                    .fechaPago(LocalDateTime.now())
                    .estado(PaymentStatus.PENDIENTE)
                    .comprobanteUrl(request.getComprobanteUrl())
                    .referenciaExterna(request.getReferenciaExterna())
                    .build();

            Payment saved = paymentRepository.save(payment);
            return convertToDTO(saved);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Método de pago inválido: " + request.getMetodo());
        }
    }

    /**
     * Obtiene pagos de una orden
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByPedidoId(orderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica (confirma o rechaza) un pago
     */
    @Transactional
    public PaymentDTO verifyPayment(Long paymentId, Long usuarioVerificadorId, VerifyPaymentRequestDTO request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        User verificador = userRepository.findById(usuarioVerificadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario verificador no encontrado"));

        try {
            PaymentStatus status = PaymentStatus.fromValue(request.getEstado());

            payment.setEstado(status);
            payment.setUsuarioVerificador(verificador);
            payment.setFechaValidacion(LocalDateTime.now());

            // Si se confirmó el pago, cambiar estado de la orden a PAGADO
            if (status == PaymentStatus.CONFIRMADO) {
                Order order = payment.getPedido();
                order.setEstado(OrderStatus.PAGADO);
                orderRepository.save(order);
            }

            Payment updated = paymentRepository.save(payment);
            return convertToDTO(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pago inválido: " + request.getEstado());
        }
    }

    /**
     * Obtiene todos los pagos (admin, paginado)
     */
    @Transactional(readOnly = true)
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Obtiene pagos por estado
     */
    @Transactional(readOnly = true)
    public Page<PaymentDTO> getPaymentsByStatus(String estado, Pageable pageable) {
        try {
            PaymentStatus status = PaymentStatus.fromValue(estado);
            return paymentRepository.findByEstado(status, pageable)
                    .map(this::convertToDTO);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pago inválido: " + estado);
        }
    }

    /**
     * Convierte Payment a DTO
     */
    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .pedidoId(payment.getPedido().getId())
                .pedidoCodigo(payment.getPedido().getCodigo())
                .metodo(payment.getMetodo().getValue())
                .monto(payment.getMonto())
                .fechaPago(payment.getFechaPago())
                .estado(payment.getEstado().getValue())
                .comprobanteUrl(payment.getComprobanteUrl())
                .referenciaExterna(payment.getReferenciaExterna())
                .usuarioVerificadorId(payment.getUsuarioVerificador() != null ? payment.getUsuarioVerificador().getId() : null)
                .usuarioVerificadorNombre(payment.getUsuarioVerificador() != null ? payment.getUsuarioVerificador().getNombre() : null)
                .fechaValidacion(payment.getFechaValidacion())
                .build();
    }

}