package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CategoryRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.CategoryResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Category;
import com.jhomilmotors.jhomilwebapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO request) {
        Category creada = categoryService.createCategory(request);
        return ResponseEntity.status(201).body(new CategoryResponseDTO(creada.getId(), creada.getNombre()));
    }



    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/slug/{slug}")
    public Category getBySlug(@PathVariable String slug) {
        return categoryService.getBySlug(slug).orElse(null);
    }

    @GetMapping("/nombre/{nombre}")
    public Category getByNombre(@PathVariable String nombre) {
        return categoryService.getByNombre(nombre).orElse(null);
    }

    @GetMapping("/subcat/{padreId}")
    public List<Category> getSubcats(@PathVariable Long padreId) {
        return categoryService.getSubcategorias(padreId);
    }

    @GetMapping("/root")
    public List<Category> getRootCats() {
        return categoryService.getCategoriasRaiz();
    }

    @GetMapping("/search")
    public List<Category> searchByNombre(@RequestParam String nombre) {
        return categoryService.searchByNombre(nombre);
    }

    @GetMapping("/orden/raiz")
    public List<Category> getRootOrdered() {
        return categoryService.getRootCategoriesOrdered();
    }
}
