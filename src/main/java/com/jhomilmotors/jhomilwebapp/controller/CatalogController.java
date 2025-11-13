package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.brand.BrandRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.brand.BrandResponseDTO;
import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import com.jhomilmotors.jhomilwebapp.dto.ProductDetailsResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
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



    @GetMapping("/{id}/imagenes-completas")
    public ResponseEntity<List<ProductDetailsResponseDTO.ImagenResponse>> getAllImagesForProductAndVariants(@PathVariable Long id) {
        List<ProductDetailsResponseDTO.ImagenResponse> imagenes = catalogService.getImagesForProductAndVariants(id);
        return ResponseEntity.ok(imagenes);
    }



    /**
     * Obtiene todos los productos activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Product>> getActivos() {
        return ResponseEntity.ok(catalogService.findByActivoTrue());
    }

    /**
     * Obtiene todos los productos inactivos (ocultos/deshabilitados).
     */
    @GetMapping("/inactivos")
    public ResponseEntity<List<Product>> getInactivos() {
        return ResponseEntity.ok(catalogService.findByActivoFalse());
    }

    /**
     * Busca productos cuyo nombre contiene la cadena indicada (paginado).
     */
    @GetMapping("/buscar-nombre")
    public ResponseEntity<Page<Product>> buscarPorNombre(@RequestParam String nombre, Pageable pageable) {
        return ResponseEntity.ok(catalogService.findByNombre(nombre, pageable));
    }

    /**
     * Obtiene productos activos con paginación, para catálogos grandes.
     */
    @GetMapping("/activos-paged")
    public ResponseEntity<Page<Product>> activosPaged(Pageable pageable) {
        return ResponseEntity.ok(catalogService.findActivoTruePaged(pageable));
    }

    /**
     * Devuelve los productos de una categoría específica.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(catalogService.findByCategoryId(categoryId));
    }

    /**
     * Devuelve los productos de una marca específica.
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<Product>> getByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(catalogService.findByBrandId(brandId));
    }

    /**
     * Obtiene productos activos de una categoría específica, paginado.
     */
    @GetMapping("/category-activo/{categoryId}")
    public ResponseEntity<Page<Product>> activosPorCategoria(@PathVariable Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(catalogService.findByCategoryAndActivoTrue(categoryId, pageable));
    }

    /**
     * Obtiene productos activos de una marca específica, paginado.
     */
    @GetMapping("/brand-activo/{brandId}")
    public ResponseEntity<Page<Product>> activosPorMarca(@PathVariable Long brandId, Pageable pageable) {
        return ResponseEntity.ok(catalogService.findByBrandAndActivoTrue(brandId, pageable));
    }

    /**
     * Búsqueda "fuzzy" por nombre o descripción (usa ILIKE) para sugerencias o búsquedas globales.
     */
    @GetMapping("/fuzzy-search")
    public ResponseEntity<Page<Product>> fuzzySearchNombreDesc(@RequestParam String nombre, Pageable pageable) {
        return ResponseEntity.ok(catalogService.fuzzySearchNombreDescripcion(nombre, pageable));
    }

    /**
     * Busca variantes cuyo SKU contiene la cadena indicada.
     */
    @GetMapping("/buscar-en-variantes")
    public ResponseEntity<List<ProductVariant>> buscarEnVariantes(@RequestParam String q) {
        return ResponseEntity.ok(catalogService.buscarEnVariantes(q));
    }

}
