package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.ProductOnSaleDTO;
import com.jhomilmotors.jhomilwebapp.dto.PromotionProductDTO;
import com.jhomilmotors.jhomilwebapp.service.PromotionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List; // Mantenemos la importación aunque no se use en los métodos paginados

@RestController
@RequestMapping("/api/promotion-products")
@RequiredArgsConstructor
public class PromotionProductController {

    private final PromotionProductService service;

    // -------------------------------------------------------------------
    // CRUD Básico Estandarizado con ResponseEntity
    // -------------------------------------------------------------------

    /**
     * Obtiene una página paginada de todos los PromotionProduct. (Sustituye al antiguo getAll() List)
     * Endpoint: GET /api/promotion-products?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<PromotionProductDTO>> getAll(Pageable pageable) {
        Page<PromotionProductDTO> resultPage = service.getAll(pageable);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * Obtiene un registro por ID.
     * Endpoint: GET /api/promotion-products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PromotionProductDTO> getById(@PathVariable Long id) {
        PromotionProductDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }


    /**
     * Crea un nuevo registro. Devuelve 201 Created.
     * Endpoint: POST /api/promotion-products
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PromotionProductDTO> create(@RequestBody PromotionProductDTO dto) {
        PromotionProductDTO createdDto = service.create(dto);
        // Retorna 201 Created
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    /**
     * Actualiza un registro existente. Devuelve 200 OK.
     * Endpoint: PUT /api/promotion-products/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PromotionProductDTO> update(@PathVariable Long id, @RequestBody PromotionProductDTO dto) {
        PromotionProductDTO updatedDto = service.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    /**
     * Elimina un registro. Devuelve 204 No Content.
     * Endpoint: DELETE /api/promotion-products/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        // Retorna 204 No Content
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------
    // QUERIES PAGINADAS (Filtrado)
    // -------------------------------------------------------------------

    /**
     * Endpoint Principal: Obtiene productos asociados a un ID de promoción.
     * Endpoint: GET /api/promotion-products/by-promotion/{promotionId}?page=0
     */
    @GetMapping("/by-promotion/{promotionId}")
    public ResponseEntity<Page<PromotionProductDTO>> getProductsByPromotionId(
            @PathVariable Long promotionId,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getProductsByPromotionId(promotionId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    // -------------------------------
    // Queries por producto (PAGINADO)
    // -------------------------------

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<Page<PromotionProductDTO>> getByProduct(
            @PathVariable Long productId,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getByProduct(productId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/active-by-product/{productId}")
    public ResponseEntity<Page<PromotionProductDTO>> getActiveByProduct(
            @PathVariable Long productId,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getActiveByProduct(productId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/inactive-by-product/{productId}")
    public ResponseEntity<Page<PromotionProductDTO>> getInactiveByProduct(
            @PathVariable Long productId,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getInactiveByProduct(productId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    // -------------------------------
    // Queries por variante (PAGINADO)
    // -------------------------------

    @GetMapping("/by-variant/{variantId}")
    public ResponseEntity<Page<PromotionProductDTO>> getByVariant(
            @PathVariable Long variantId,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getByVariant(variantId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/active-by-variant/{variantId}")
    public ResponseEntity<Page<PromotionProductDTO>> getActiveByVariant(
            @PathVariable Long variantId,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getActiveByVariant(variantId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/inactive-by-variant/{variantId}")
    public ResponseEntity<Page<PromotionProductDTO>> getInactiveByVariant(
            @PathVariable Long variantId,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getInactiveByVariant(variantId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    // -------------------------------
    // Otras queries útiles
    // -------------------------------

    @GetMapping("/required-amount/{cantidad}")
    public ResponseEntity<Page<PromotionProductDTO>> getPromosByRequiredAmount(
            @PathVariable int cantidad,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getPromosByRequiredAmount(cantidad, pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/with-product-gift")
    public ResponseEntity<Page<PromotionProductDTO>> getPromosWithProductGift(Pageable pageable) {
        Page<PromotionProductDTO> resultPage = service.getPromosWithProductGift(pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/with-variant-gift")
    public ResponseEntity<Page<PromotionProductDTO>> getPromosWithVariantGift(Pageable pageable) {
        Page<PromotionProductDTO> resultPage = service.getPromosWithVariantGift(pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<PromotionProductDTO>> getActivePromos(Pageable pageable) {
        Page<PromotionProductDTO> resultPage = service.getActivePromos(pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/inactive")
    public ResponseEntity<Page<PromotionProductDTO>> getInactivePromos(Pageable pageable) {
        Page<PromotionProductDTO> resultPage = service.getInactivePromos(pageable);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/search-by-product-name")
    public ResponseEntity<Page<PromotionProductDTO>> searchByProductName(
            @RequestParam("name") String productName,
            Pageable pageable) {

        Page<PromotionProductDTO> resultPage = service.getByProductNameContaining(productName, pageable);
        return ResponseEntity.ok(resultPage);
    }

    // Promociones por producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PromotionProductDTO>> getPromosByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getPromotionsByProductId(productId));
    }

    // Promociones por variante
    @GetMapping("/variant/{variantId}")
    public ResponseEntity<List<PromotionProductDTO>> getPromosByVariant(@PathVariable Long variantId) {
        return ResponseEntity.ok(service.getPromotionsByVariantId(variantId));
    }

    /**
     * Endpoint optimizado para la App Móvil.
     * Retorna productos en oferta activos con precios ya calculados.
     * GET /api/promotion-products/on-sale?page=0&size=10
     */
    @GetMapping("/on-sale")
    public ResponseEntity<Page<ProductOnSaleDTO>> getProductsOnSale(Pageable pageable) {
        Page<ProductOnSaleDTO> result = service.getProductsOnSale(pageable);
        return ResponseEntity.ok(result);
    }

}