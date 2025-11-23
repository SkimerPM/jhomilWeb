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
import java.util.Optional;

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

    Optional<Product> findBySkuBase(String skuBase);
    List<Product> findByCategoryId(Long categoriaId);
    List<Product> findByBrand_Id(Long marcaId); // <-- CORRECTO
    List<Product> findByActivoTrue();
    List<Product> findByActivoFalse();

    Page<Product> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Product> findByCategoryIdAndActivoTrue(Long categoryId, Pageable pageable);
    Page<Product> findByBrand_IdAndActivoTrue(Long marcaId, Pageable pageable); // <-- CORRECTO
    Page<Product> findByActivoTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.nombre ILIKE %:nombre% OR p.descripcion ILIKE %:nombre%")
    Page<Product> searchByNombreOrDescripcion(@Param("nombre") String nombre, Pageable pageable);


    /**
     * Consulta que resuelve el problema de precios en 0 y stocks vacíos.
     * 1. Precio: Si hay variantes, toma el menor. Si no, toma el del padre.
     * 2. Stock: Suma el stock de todas las variantes activas.
     */
    @Query("SELECT new com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse(" +
            "  p.id, " +
            "  p.nombre, " +
            "  p.descripcion, " +
            // Precio Inteligente:
            "  COALESCE((SELECT min(v.precio) FROM ProductVariant v WHERE v.product = p AND v.activo = true), p.precioBase), " +
            // Stock Real (Suma):
            "  COALESCE((SELECT CAST(SUM(v.stock) AS long) FROM ProductVariant v WHERE v.product = p AND v.activo = true), 0L), " +
            // Imagen Principal:
            "  (SELECT i.url FROM Image i WHERE i.product = p AND i.esPrincipal = true), " +
            "  p.category.id, " +
            "  p.category.nombre, " +
            "  p.brand.id, " +
            "  p.brand.nombre " +
            ") " +
            "FROM Product p " +
            "WHERE p.activo = true")
    List<ProductCatalogResponse> findAllCatalogProductsOptimized();
}