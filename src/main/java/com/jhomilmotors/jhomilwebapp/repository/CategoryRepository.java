package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByNombreIgnoreCase(String nombre);

    // Subcategorías
    List<Category> findByPadreId(Long padreId);
    List<Category> findByPadreIsNull();

    // Búsqueda de categorías con nombre
    List<Category> findByNombreContainingIgnoreCase(String nombre);

    // Árbol de categorías
    @Query("SELECT c FROM Category c WHERE c.padre IS NULL ORDER BY c.nombre")
    List<Category> findRootCategories();

}