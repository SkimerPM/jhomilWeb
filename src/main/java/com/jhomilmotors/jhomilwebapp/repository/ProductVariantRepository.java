package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    /** * Busca la primera variante activa para un producto, ordenada por precio ascendente.
     * Esto se usa para mostrar el precio "Desde X" en el catálogo.
     */
    Optional<ProductVariant> findFirstByProductAndActivoTrueOrderByPrecioAsc(Product product);
}