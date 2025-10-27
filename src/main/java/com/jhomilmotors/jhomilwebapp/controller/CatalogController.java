package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import com.jhomilmotors.jhomilwebapp.dto.ProductDetailsResponseDTO;
import com.jhomilmotors.jhomilwebapp.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@CrossOrigin(origins = "http://localhost:5173")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    // Catálogo general
    @GetMapping
    public ResponseEntity<List<ProductCatalogResponse>> getCatalog() {
        List<ProductCatalogResponse> productos = catalogService.findAllCatalogProducts();
        return ResponseEntity.ok(productos);
    }

    // Detalles de producto
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsResponseDTO> getProductDetails(@PathVariable Long id) {
        try {
            ProductDetailsResponseDTO details = catalogService.getProductDetails(id);
            return ResponseEntity.ok(details);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
