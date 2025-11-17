package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.ProductAttribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    // Listar todos los atributos de un producto
    Page<ProductAttribute> findByProductId(Long productId, Pageable pageable);

    // 👇 AGREGA ESTE: El método simple que devuelve una Lista sin paginación
    List<ProductAttribute> findByProductId(Long productId);

    // Buscar todos los atributos de un producto por tipo
    Page<ProductAttribute> findByProductIdAndAttribute_Tipo(Long productId, AttributeType tipo, Pageable pageable);

    // Buscar todos los atributos de un producto por nombre parcial
    Page<ProductAttribute> findByProductIdAndAttribute_NombreContainingIgnoreCase(Long productId, String nombre, Pageable pageable);

    // Listar todas las relaciones de un atributo en todos los productos
    Page<ProductAttribute> findByAttributeId(Long attributeId, Pageable pageable);

    // Validar unicidad antes de crear
    Optional<ProductAttribute> findByProductIdAndAttributeId(Long productId, Long attributeId);
}