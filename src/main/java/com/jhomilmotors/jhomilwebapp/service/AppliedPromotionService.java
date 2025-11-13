package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.AppliedPromotion;
import com.jhomilmotors.jhomilwebapp.repository.AppliedPromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppliedPromotionService {
    private final AppliedPromotionRepository repo;

    public List<AppliedPromotion> getAll() {
        return repo.findAll();
    }

    public List<AppliedPromotion> getByOrderId(Long pedidoId) {
        return repo.findByPedidoId(pedidoId);
    }

    public List<AppliedPromotion> getByPromotionId(Long promoId) {
        return repo.findByPromocionId(promoId);
    }
}
