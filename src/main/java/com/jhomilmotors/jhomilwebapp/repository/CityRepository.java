package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByRegionId(Long regionId);
    Optional<City> findByNombreAndRegionId(String nombre, Long regionId);
    List<City> findByNombreContainingIgnoreCase(String nombre);
}