package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // ----------------------------------------------------
    // 1. QUERY DE CATÁLOGO PÚBLICO (Mantiene la lógica COALESCE y GROUP BY)
    // ----------------------------------------------------
    @Query("SELECT new com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse(" +
            "p.id, p.nombre, p.descripcion, p.precioBase, " +
            "COALESCE(SUM(v.stock), 0), img.url, " +
            "c.id, c.nombre, m.id, m.nombre, " +
            "COALESCE(v.sku, p.skuBase)) " + // <-- La lógica del SKU consolidado
            "FROM Product p " +
            "LEFT JOIN Category c ON c.id = p.category.id " +
            "LEFT JOIN Brand m ON m.id = p.brand.id " +
            "LEFT JOIN Image img ON img.product.id = p.id AND img.esPrincipal = true " +
            "LEFT JOIN ProductVariant v ON v.product.id = p.id AND v.activo = true " +
            "WHERE p.activo = true " +
            "GROUP BY p.id, p.nombre, p.descripcion, p.precioBase, img.url, c.id, c.nombre, m.id, m.nombre, COALESCE(v.sku, p.skuBase)")
    Page<ProductCatalogResponse> listarCatalogo(Pageable pageable);


    // ----------------------------------------------------
    // 2. QUERY DE ADMINISTRACIÓN (Carga la entidad completa con variantes)
    // ----------------------------------------------------
    // Usamos FETCH JOIN para cargar p, c, m, y las variantes en una sola consulta
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH p.brand m " +
            "LEFT JOIN FETCH p.variantes v " + // Carga explícita de variantes
            "WHERE p.activo = true " +
            "ORDER BY p.id DESC")
    Page<Product> findAllWithVariantsForAdmin(Pageable pageable);


    @Query("SELECT p FROM Product p WHERE p.activo = true")
    List<Product> findAllEntities();

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(p.skuBase) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Product> buscarTextoProductos(@Param("q") String q);

    @Query("SELECT v FROM ProductVariant v WHERE " +
            "LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<ProductVariant> buscarEnVariantes(@Param("q") String q);

    boolean existsBySkuBase(String skuBase);
}
