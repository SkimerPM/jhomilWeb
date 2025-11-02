package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import com.jhomilmotors.jhomilwebapp.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    // -----------------------------
    // Por producto
    // -----------------------------
    List<PromotionProduct> findByProducto(Product producto);
    List<PromotionProduct> findByProductoAndPromocionActivoTrue(Product producto); // solo activas
    List<PromotionProduct> findByProductoAndPromocionActivoFalse(Product producto); // solo inactivas

    // -----------------------------
    // Por variante
    // -----------------------------
    List<PromotionProduct> findByVariante(ProductVariant variante);
    List<PromotionProduct> findByVarianteAndPromocionActivoTrue(ProductVariant variante);
    List<PromotionProduct> findByVarianteAndPromocionActivoFalse(ProductVariant variante);

    // -----------------------------
    // Promociones que dan regalos (producto o variante)
    // -----------------------------
    List<PromotionProduct> findByProductoGratisIsNotNull();
    List<PromotionProduct> findByVarianteGratisIsNotNull();

    // -----------------------------
    // Buscar por cantidad requerida para aplicar la promoción
    // -----------------------------
    List<PromotionProduct> findByCantidadRequeridaGreaterThan(int cantidad);

    // -----------------------------
    // Buscar promociones activas e inactivas
    // -----------------------------
    List<PromotionProduct> findByPromocionActivoTrue();
    List<PromotionProduct> findByPromocionActivoFalse();

}
