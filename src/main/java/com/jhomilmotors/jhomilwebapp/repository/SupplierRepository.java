package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByRuc(String ruc);

    List<Supplier> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByRuc(String ruc);

    boolean existsByEmail(String email);
}
