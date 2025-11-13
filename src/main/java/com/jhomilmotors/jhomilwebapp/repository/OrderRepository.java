package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Order;
import com.jhomilmotors.jhomilwebapp.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCodigo(String codigo);

    Page<Order> findByUsuarioId(Long usuarioId, Pageable pageable);

    List<Order> findByEstado(OrderStatus estado);

    Page<Order> findByEstado(OrderStatus estado, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.fechaPedido BETWEEN :inicio AND :fin ORDER BY o.fechaPedido DESC")
    List<Order> findOrdersBetweenDates(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.usuario.id = :usuarioId AND o.estado = :estado")
    Page<Order> findByUsuarioAndEstado(@Param("usuarioId") Long usuarioId, @Param("estado") OrderStatus estado, Pageable pageable);
}