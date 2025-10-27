package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.activo = true")
    List<Product> findAllEntities();

    // Buscar productos por nombre, descripción o SKU base
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(p.skuBase) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Product> buscarTextoProductos(@Param("q") String q);

    // Buscar variantes por SKU o buscar por atributo si quieres extender
    @Query("SELECT v FROM ProductVariant v WHERE " +
            "LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<ProductVariant> buscarEnVariantes(@Param("q") String q);

}