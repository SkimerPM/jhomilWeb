package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {
    List<VariantAttribute> findByVarianteId(Long varianteId);
}