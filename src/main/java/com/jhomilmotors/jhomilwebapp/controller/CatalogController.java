package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.brand.BrandRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.brand.BrandResponseDTO;
import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import com.jhomilmotors.jhomilwebapp.dto.ProductDetailsResponseDTO;
import com.jhomilmotors.jhomilwebapp.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // soporta tanto listar todo, como listar por nombre.
    // /admin/brands?page=0&size=10"
    // /admin/brands?name=algo&page=0&size=10"
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/brands")
    public Page<BrandResponseDTO> getBrands(@RequestParam(defaultValue = "") String name,
                                            @PageableDefault(size = 10) Pageable pageable) {
        return catalogService.findBrands(name, pageable );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/brands")
    public ResponseEntity<BrandResponseDTO> createBrand(@RequestBody BrandRequestDTO request) {
        BrandResponseDTO created = catalogService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/admin/brands/{id}")
    public ResponseEntity<BrandResponseDTO> updateBrand(@PathVariable Long id, @RequestBody BrandRequestDTO request) {
        BrandResponseDTO updated = catalogService.updateBrand(id, request);
        return ResponseEntity.ok(updated);
    }


    // ¡CUIDADO!: Borrado NO lógico.
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/admin/brands/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        catalogService.deleteBrand(id);
        return ResponseEntity.noContent().build(); // 204 No Content si fue exitoso
    }



}
