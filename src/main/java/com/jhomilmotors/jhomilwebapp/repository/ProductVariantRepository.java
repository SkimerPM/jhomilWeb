package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    /** * Busca la primera variante activa para un producto, ordenada por precio ascendente.
     * Esto se usa para mostrar el precio "Desde X" en el catálogo.
     */
    Optional<ProductVariant> findFirstByProductAndActivoTrueOrderByPrecioAsc(Product product);

    @Query("SELECT v FROM ProductVariant v WHERE " +
            "LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<ProductVariant> buscarTextoVariantes(@Param("q") String q);


    Optional<ProductVariant> findBySku(String sku);
    List<ProductVariant> findByProductId(Long productoId);
    List<ProductVariant> findByProductIdAndActivoTrue(Long productoId);
    List<ProductVariant> findByActivoTrue();
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stock <= 10")
    List<ProductVariant> findLowStockVariants();

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stock = 0")
    List<ProductVariant> findOutOfStockVariants();

    Page<ProductVariant> findByProductId(Long productoId, Pageable pageable);

    // PASO 1: Buscamos la variante, traemos al Padre (Product) y los Atributos
    // Quitamos el FETCH de imagenes aquí
    @Query("SELECT DISTINCT v FROM ProductVariant v " +
            "JOIN FETCH v.product p " +
            "LEFT JOIN FETCH v.atributos " +
            "WHERE LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<ProductVariant> buscarConAtributos(@Param("q") String q);

    // PASO 2: Una consulta auxiliar para cargar las imágenes de esas variantes
    @Query("SELECT DISTINCT v FROM ProductVariant v " +
            "LEFT JOIN FETCH v.imagenes " +
            "WHERE v IN :variantes")
    List<ProductVariant> cargarImagenes(@Param("variantes") List<ProductVariant> variantes);
}