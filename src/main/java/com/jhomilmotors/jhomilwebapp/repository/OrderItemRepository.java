package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByPedidoId(Long pedidoId);
    List<OrderItem> findByVarianteId(Long varianteId);
    List<OrderItem> findByLoteOrigenId(Long loteId);
}