package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUsuarioIdAndActivoTrue(Long usuarioId);
    Optional<Cart> findBySessionIdAndActivoTrue(String sessionId);
    List<Cart> findByActivoFalse();

    Optional<Cart> findByUsuarioId(Long usuarioId);
}
