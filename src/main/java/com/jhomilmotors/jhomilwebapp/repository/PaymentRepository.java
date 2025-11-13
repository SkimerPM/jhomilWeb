package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Payment;
import com.jhomilmotors.jhomilwebapp.enums.PaymentMethod;
import com.jhomilmotors.jhomilwebapp.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPedidoId(Long pedidoId);

    List<Payment> findByEstado(PaymentStatus estado);

    List<Payment> findByMetodo(PaymentMethod metodo);

    Page<Payment> findAll(Pageable pageable);

    Page<Payment> findByEstado(PaymentStatus estado, Pageable pageable);

    Page<Payment> findByMetodo(PaymentMethod metodo, Pageable pageable);

    Optional<Payment> findByReferenciaExterna(String referencia);

    List<Payment> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Payment> findByUsuarioVerificadorId(Long usuarioId);
}