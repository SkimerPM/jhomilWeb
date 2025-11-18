package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Optional<Attribute> findByCodigo(String codigo);
    Page<Attribute> findByTipo(AttributeType tipo, Pageable pageable );
    Page<Attribute> findByEsVariacionTrue(Pageable pageable );
    Page<Attribute> findByNombreContainingIgnoreCase(String nombre, Pageable pageable );
    List<Attribute> findAllByOrderByOrdenVisualAsc();
}
