package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    // Buscar marcas por nombre, ignorando mayúsculas/minúsculas y con paginación
    Page<Brand> findByNombreContainingIgnoreCase(String search, Pageable pageable);

    Optional<Brand> findByNombreIgnoreCase(String search);

}