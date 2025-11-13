package com.jhomilmotors.jhomilwebapp.repository;
import com.jhomilmotors.jhomilwebapp.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCarritoId(Long carritoId);
    Optional<CartItem> findByCarritoIdAndVarianteId(Long carritoId, Long varianteId);
    void deleteByCarritoId(Long carritoId);
}