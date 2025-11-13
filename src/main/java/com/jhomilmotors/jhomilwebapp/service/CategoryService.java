package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.Category;
import com.jhomilmotors.jhomilwebapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
