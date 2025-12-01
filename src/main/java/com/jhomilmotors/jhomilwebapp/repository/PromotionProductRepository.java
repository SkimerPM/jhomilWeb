package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import com.jhomilmotors.jhomilwebapp.entity.Promotion;
import com.jhomilmotors.jhomilwebapp.entity.PromotionProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    // -----------------------------
    // Por Promoción (Principal)
    // -----------------------------
    /**
     * Devuelve una página de configuraciones de producto asociadas a la entidad Promotion.
     * Esto es utilizado por el método getProductsByPromotionId en el servicio.
     */
    Page<PromotionProduct> findByPromocion(Promotion promocion, Pageable pageable);


    // -----------------------------
    // Por producto (PAGINADO)
    // -----------------------------
    Page<PromotionProduct> findByProducto(Product producto, Pageable pageable);
    Page<PromotionProduct> findByProductoAndPromocionActivoTrue(Product producto, Pageable pageable); // solo activas
    Page<PromotionProduct> findByProductoAndPromocionActivoFalse(Product producto, Pageable pageable); // solo inactivas

    // -----------------------------
    // Por variante (PAGINADO)
    // -----------------------------
    Page<PromotionProduct> findByVariante(ProductVariant variante, Pageable pageable);
    Page<PromotionProduct> findByVarianteAndPromocionActivoTrue(ProductVariant variante, Pageable pageable);
    Page<PromotionProduct> findByVarianteAndPromocionActivoFalse(ProductVariant variante, Pageable pageable);

    // -----------------------------
    // Promociones que dan regalos (PAGINADO)
    // -----------------------------
    Page<PromotionProduct> findByProductoGratisIsNotNull(Pageable pageable);
    Page<PromotionProduct> findByVarianteGratisIsNotNull(Pageable pageable);

    // -----------------------------
    // Buscar por cantidad requerida (PAGINADO)
    // -----------------------------
    Page<PromotionProduct> findByCantidadRequeridaGreaterThan(int cantidad, Pageable pageable);

    // -----------------------------
    // Buscar promociones activas e inactivas (PAGINADO)
    // -----------------------------
    Page<PromotionProduct> findByPromocionActivoTrue(Pageable pageable);
    Page<PromotionProduct> findByPromocionActivoFalse(Pageable pageable);

    // Nueva firma para buscar en la relación Producto por coincidencia de nombre (LIKE %nombre%)
    Page<PromotionProduct> findByProductoNombreContaining(String nombreProducto, Pageable pageable);

    // NOTA: El método Page<PromotionProduct> findAll(Pageable pageable)
    // está heredado automáticamente de JpaRepository.

    List<PromotionProduct> findByPromocionId(Long promocionId);
    List<PromotionProduct> findByProductoId(Long productoId);
    List<PromotionProduct> findByVarianteId(Long varianteId);
    Page<PromotionProduct> findByPromocionId(Long promocionId, Pageable pageable);
    Page<PromotionProduct> findByProductoId(Long productoId, Pageable pageable);


    /**
     * Query optimizada para Ofertas Activas.
     * 1. Filtra por estado activo de Promo, Producto y Variante.
     * 2. Filtra por rango de fechas (considerando nulos como "siempre válido").
     * 3. Hace FETCH de las relaciones para evitar N+1 queries.
     */
    @Query("SELECT pp FROM PromotionProduct pp " +
            "JOIN FETCH pp.promocion promo " +
            "JOIN FETCH pp.producto prod " +
            "LEFT JOIN FETCH pp.variante var " +
            "WHERE promo.activo = true " +
            "AND (promo.fechaInicio IS NULL OR promo.fechaInicio <= :now) " +
            "AND (promo.fechaFin IS NULL OR promo.fechaFin >= :now) " +
            "AND prod.activo = true " +
            "AND (var IS NULL OR var.activo = true)")
    Page<PromotionProduct> findActiveOnSaleProducts(@Param("now") LocalDateTime now, Pageable pageable);
}