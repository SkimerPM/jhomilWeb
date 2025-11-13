package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Optional<Attribute> findByCodigo(String codigo);
    List<Attribute> findByTipo(AttributeType tipo);
    List<Attribute> findByEsVariacionTrue();
    List<Attribute> findByNombreContainingIgnoreCase(String nombre);
    List<Attribute> findAllByOrderByOrdenVisualAsc();
}
