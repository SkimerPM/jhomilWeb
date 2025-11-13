package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    List<ProductAttribute> findByProductId(Long productoId);
    List<ProductAttribute> findByAttributeId(Long atributoId);
    Optional<ProductAttribute> findByProductIdAndAttributeId(Long productoId, Long atributoId);

}