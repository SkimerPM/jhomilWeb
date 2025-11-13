package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Receipt;
import com.jhomilmotors.jhomilwebapp.enums.ReceiptStatus;
import com.jhomilmotors.jhomilwebapp.enums.ReceiptType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByNumero(String numero);
    List<Receipt> findByPedidoId(Long pedidoId);
    List<Receipt> findByTipo(ReceiptType tipo);
    List<Receipt> findByEstado(ReceiptStatus estado);
    boolean existsByNumero(String numero);
}
