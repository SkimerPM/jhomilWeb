package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CategoryAdminDTO;
import com.jhomilmotors.jhomilwebapp.dto.CategoryRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.CategoryResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Category;
import com.jhomilmotors.jhomilwebapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jhomilmotors.jhomilwebapp.dto.CategoryRequestDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // ⭐️ REEMPLAZO DEL MÉTODO ANTIGUO: USAMOS EL FLUJO DE SUBIDA DE IMAGEN
    @PostMapping(consumes = {"multipart/form-data"}) // ⬅️ IMPORTANTE: Consumir datos multipart
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @ModelAttribute CategoryRequestDTO request) { // ⬅️ Usamos @ModelAttribute para el archivo
        try {
            // ⭐️ LLAMAR AL MÉTODO DEL SERVICIO QUE MANEJA CLOUDINARY
            Category creada = categoryService.createCategoryWithImage(request);

            // Usamos el constructor de 3 argumentos del DTO para devolver la URL completa
            return ResponseEntity.status(201).body(
                    new CategoryResponseDTO(creada.getId(), creada.getNombre(), creada.getImagenUrlBase())
            );
        } catch (IOException e) {
            // Manejar error de subida/archivo
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        } catch (IllegalArgumentException e) {
            // Manejar errores de validación, etc.
            return ResponseEntity.status(400).build();
        }
    }



//    @GetMapping
//    public List<Category> getAll() {
//        return categoryService.getAll();
//    }

    @GetMapping
    public Page<CategoryAdminDTO> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return categoryService.getAllAdminPaged(pageable);
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
