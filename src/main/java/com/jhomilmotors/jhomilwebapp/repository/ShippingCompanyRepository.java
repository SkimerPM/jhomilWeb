package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.ShippingCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingCompanyRepository extends JpaRepository<ShippingCompany, Long> {
    Optional<ShippingCompany> findByNombreIgnoreCase(String nombre);
}