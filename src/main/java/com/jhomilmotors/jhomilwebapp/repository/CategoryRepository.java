package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.dto.CategoryAdminDTO; // ¡Importar DTO!
import com.jhomilmotors.jhomilwebapp.entity.Category;
import org.springframework.data.domain.Page; // ¡Importar Page!
import org.springframework.data.domain.Pageable; // ¡Importar Pageable!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByNombreIgnoreCase(String nombre);

    List<Category> findByPadreId(Long padreId);
    List<Category> findByPadreIsNull();

    List<Category> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT c FROM Category c WHERE c.padre IS NULL ORDER BY c.nombre")
    List<Category> findRootCategories();

    // --- ¡ESTA ES LA SOLUCIÓN! ---
    // Esto construye el DTO directamente desde la DB, evitando el error LAZY y el bucle infinito
    // (LEFT JOIN c.padre p) maneja los padres nulos (categorías raíz)
    @Query("SELECT NEW com.jhomilmotors.jhomilwebapp.dto.CategoryAdminDTO(" +
            "c.id, c.nombre, c.slug, c.descripcion, " +
            "p.id, p.nombre, " +
            "NULL) " + // Dejamos subcategorías en NULL, no las necesitamos en la tabla principal
            "FROM Category c LEFT JOIN c.padre p")
    Page<CategoryAdminDTO> findAllAsAdminDTO(Pageable pageable);
}