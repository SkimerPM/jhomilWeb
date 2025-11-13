package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {
    List<VariantAttribute> findByVarianteId(Long varianteId);
    List<VariantAttribute> findByAttributeId(Long atributoId);
    Optional<VariantAttribute> findByVarianteIdAndAttributeId(Long varianteId, Long atributoId);

}