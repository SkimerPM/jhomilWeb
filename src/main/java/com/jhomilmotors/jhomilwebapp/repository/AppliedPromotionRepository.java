package com.jhomilmotors.jhomilwebapp.repository;
import com.jhomilmotors.jhomilwebapp.entity.AppliedPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppliedPromotionRepository extends JpaRepository<AppliedPromotion, Long> {
    List<AppliedPromotion> findByPedidoId(Long pedidoId);
    List<AppliedPromotion> findByPromocionId(Long promocionId);
}