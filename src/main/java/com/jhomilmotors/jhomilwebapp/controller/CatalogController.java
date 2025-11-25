package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.dto.brand.BrandRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.brand.BrandResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Image;
import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.service.CatalogService;
import com.jhomilmotors.jhomilwebapp.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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


    /**
     * Obtiene la lista completa de categorías con la URL de imagen construida.
     * Ideal para el menú de navegación en aplicaciones móviles.
     */
    @GetMapping("/categories/full")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategoriesForMobile() {
        List<CategoryResponseDTO> categorias = catalogService.findAllCategoriesForMobile();
        return ResponseEntity.ok(categorias);
    }

    // =======================
    //    Imágenes Producto
    // =======================

    /**
     * Agrega una imagen a un producto.
     *
     * @param productId ID del producto dueño de la imagen.
     * @param dto Datos de la imagen ({@code url}, principalidad, orden).
     * @return ID de la imagen creada.
     * @response 201 Imagen creada correctamente.
     * @response 400 Producto no encontrado o datos inválidos.
     *
     * Ejemplo de request:
     * <pre>
     * {
     *   "url": "https://miweb.com/foto.jpg",
     *   "esPrincipal": true,
     *   "orden": 1
     * }
     * </pre>
     * Ejemplo de respuesta:
     * <pre>
     * { "imagenId": 123 }
     * </pre>
     */
    @PostMapping("/product/{productId}/images")
    public ResponseEntity<?> addImageToProduct(@PathVariable Long productId, @RequestBody ImagenIndividualDTO dto) {
        try {
            Image saved = catalogService.addImageToProduct(productId, dto);
            return ResponseEntity.status(201).body(Map.of("imagenId", saved.getId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Agrega una imagen a una variante dada por su ID.
     *
     * @param variantId ID de la variante.
     * @param dto Datos de la imagen.
     * @return ID de la imagen creada.
     * @response 201 Imagen creada, 400 si variante no existe o datos inválidos.
     */
    @PostMapping("/variant/{variantId}/images")
    public ResponseEntity<?> addImageToVariant(@PathVariable Long variantId, @RequestBody ImagenIndividualDTO dto) {
        try {
            Image saved = catalogService.addImageToVariant(variantId, dto);
            return ResponseEntity.status(201).body(Map.of("imagenId", saved.getId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Actualiza los datos de una imagen (producto o variante) según su ID.
     *
     * @param imageId ID de la imagen a editar.
     * @param dto Nuevos datos de la imagen (url, principalidad, orden).
     * @return Datos de la imagen actualizada.
     * @response 200 Imagen actualizada.
     * @response 400 Imagen inexistente o datos inválidos.
     */
    @PutMapping("/images/{imageId}")
    public ResponseEntity<?> updateImage(@PathVariable Long imageId, @RequestBody ImagenIndividualDTO dto) {
        try {
            Image img = catalogService.updateImage(imageId, dto);
            return ResponseEntity.ok(Map.of("imagenId", img.getId(), "url", img.getUrl()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Elimina una imagen de producto o variante.
     *
     * @param imageId ID de la imagen a eliminar.
     * @response 204 Imagen eliminada.
     * @response 400 Imagen no existe.
     */
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        try {
            catalogService.deleteImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ==============
    //   Ejemplo para el DTO de imagen individual
    // ==============
    /**
     * DTO usado para agregar/editar una sola imagen de producto o variante.
     *
     * {
     *   "url": "string",
     *   "esPrincipal": true,
     *   "orden": 1
     * }
     */

    // =======================
    //    Gestión de productos
    // =======================

    /**
     * Crea un producto junto a sus variantes e imágenes.
     *
     * @param request DTO con todos los datos del producto.
     * @return ID y nombre del producto creado.
     * @response 201 Producto creado, 400 si datos inválidos/duplicados.
     *
     * Ejemplo de request:
     * {
     *   "nombre": "Aceite Yamalube 20W-50",
     *   "skuBase": "YAM-20W50",
     *   "precioBase": 35.00,
     *   "imagenes": [{ ... }]
     *   "variantes": [{ ... }]
     * }
     */
    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreationRequestDTO request) {
        try {
            Product created = catalogService.createProductWithVariantsAndImages(request);
            return ResponseEntity.status(201).body(Map.of("productId", created.getId(), "nombre", created.getNombre()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/product/simple")
    public ResponseEntity<ProductDetailsResponseDTO> createSimpleProduct(@RequestBody SimpleProductRequestDTO request) {
        ProductDetailsResponseDTO response = catalogService.createSimpleProduct(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza los datos de un producto y sus variantes.
     *
     * @param id ID del producto.
     * @param request DTO de actualización completo.
     * @return ID del producto actualizado.
     * @response 200 OK, 400 si datos inválidos/error de validación.
     */
    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequestDTO request) {
        try {
            Product updated = catalogService.updateProduct(id, request);
            return ResponseEntity.ok(Map.of("productId", updated.getId(), "nombre", updated.getNombre()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Elimina un producto y sus variantes.
     *
     * @param id ID del producto.
     * @response 204 Si se elimina correctamente, 404 si no existe.
     */
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            catalogService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Obtiene los detalles completos de un producto (info, variantes, imágenes).
     * @param id ID de producto.
     * @return DTO completo de detalles (ProductDetailsResponseDTO).
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDetailsResponseDTO> getProductDetails(@PathVariable Long id) {
        try {
            ProductDetailsResponseDTO details = catalogService.getProductDetails(id);
            return ResponseEntity.ok(details);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponseDTO>> getBrandsCustomer() {
        return ResponseEntity.ok(catalogService.findAllBrands());
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
     * Obtiene todos los productos (A/I) paginados.
     */

    @GetMapping("/admin/products")
    public Page<AdminProductListDTO> getAllAdminProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return catalogService.getAllAdminProductsPaged(pageable);
    }



    /**
     * Obtiene todos los productos activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Product>> getActivos() {
        return ResponseEntity.ok(catalogService.findByActivoTrue());
    }

    /**
     * Obtiene las variantes de un producto
     */
    @GetMapping("/product/{productId}/variants")
    public ResponseEntity<List<ProductVariant>> getVariantsByProduct(@PathVariable Long productId) {
        List<ProductVariant> variantes = catalogService.findVariantsByProductId(productId);
        return ResponseEntity.ok(variantes);
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
    public ResponseEntity<Page<ProductCatalogResponse>> activosPorCategoria(@PathVariable Long categoryId, Pageable pageable) {
        Page<ProductCatalogResponse> page = catalogService.findProductCatalogByCategory(categoryId, pageable);
        return ResponseEntity.ok(page);
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


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/low-stock")
    public ResponseEntity<List<LowStockResponseDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "5") int limit) { // Por defecto avisa si hay 5 o menos
        return ResponseEntity.ok(catalogService.getLowStockProducts(limit));
    }

}
