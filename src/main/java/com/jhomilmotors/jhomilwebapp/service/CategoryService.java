package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CategoryAdminDTO;
import com.jhomilmotors.jhomilwebapp.dto.CategoryRequestDTO;
import com.jhomilmotors.jhomilwebapp.entity.Category;
import com.jhomilmotors.jhomilwebapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
    public Optional<Category> getBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }
    public Optional<Category> getByNombre(String nombre) {
        return categoryRepository.findByNombreIgnoreCase(nombre);
    }
    public List<Category> getSubcategorias(Long padreId) {
        return categoryRepository.findByPadreId(padreId);
    }
    public List<Category> getCategoriasRaiz() {
        return categoryRepository.findByPadreIsNull();
    }
    public List<Category> searchByNombre(String nombre) {
        return categoryRepository.findByNombreContainingIgnoreCase(nombre);
    }
    public List<Category> getRootCategoriesOrdered() {
        return categoryRepository.findRootCategories();
    }

    @Transactional
    public Category createCategory(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setNombre(dto.getNombre());
        category.setSlug(dto.getSlug());
        category.setDescripcion(dto.getDescripcion());
        if (dto.getPadreId() != null) {
            Category padre = categoryRepository.findById(dto.getPadreId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría padre no encontrada"));
            category.setPadre(padre);
        }
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryAdminDTO> getAllAdminPaged(Pageable pageable) {
        // Esto llama a la nueva @Query segura en el repositorio
        return categoryRepository.findAllAsAdminDTO(pageable);
    }

    // --- (El método 'toAdminDTO' que causaba el crash ya no es necesario) ---
}