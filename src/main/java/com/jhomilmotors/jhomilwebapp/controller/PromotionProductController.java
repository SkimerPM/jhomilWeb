package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.PromotionProductDTO;
import com.jhomilmotors.jhomilwebapp.service.PromotionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/promotion-products")
@RequiredArgsConstructor
public class PromotionProductController {

    private final PromotionProductService service;

    // -------------------------------
    // CRUD básico
    // -------------------------------

    @GetMapping
    public List<PromotionProductDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PromotionProductDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PromotionProductDTO create(@RequestBody PromotionProductDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PromotionProductDTO update(@PathVariable Long id, @RequestBody PromotionProductDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // -------------------------------
    // Queries por producto
    // -------------------------------

    @GetMapping("/by-product/{productId}")
    public List<PromotionProductDTO> getByProduct(@PathVariable Long productId) {
        return service.getByProduct(productId);
    }

    @GetMapping("/active-by-product/{productId}")
    public List<PromotionProductDTO> getActiveByProduct(@PathVariable Long productId) {
        return service.getActiveByProduct(productId);
    }

    @GetMapping("/inactive-by-product/{productId}")
    public List<PromotionProductDTO> getInactiveByProduct(@PathVariable Long productId) {
        return service.getInactiveByProduct(productId);
    }

    // -------------------------------
    // Queries por variante
    // -------------------------------

    @GetMapping("/by-variant/{variantId}")
    public List<PromotionProductDTO> getByVariant(@PathVariable Long variantId) {
        return service.getByVariant(variantId);
    }

    @GetMapping("/active-by-variant/{variantId}")
    public List<PromotionProductDTO> getActiveByVariant(@PathVariable Long variantId) {
        return service.getActiveByVariant(variantId);
    }

    @GetMapping("/inactive-by-variant/{variantId}")
    public List<PromotionProductDTO> getInactiveByVariant(@PathVariable Long variantId) {
        return service.getInactiveByVariant(variantId);
    }

    // -------------------------------
    // Otras queries útiles para frontend
    // -------------------------------

    @GetMapping("/required-amount/{cantidad}")
    public List<PromotionProductDTO> getPromosByRequiredAmount(@PathVariable int cantidad) {
        return service.getPromosByRequiredAmount(cantidad);
    }

    @GetMapping("/with-product-gift")
    public List<PromotionProductDTO> getPromosWithProductGift() {
        return service.getPromosWithProductGift();
    }

    @GetMapping("/with-variant-gift")
    public List<PromotionProductDTO> getPromosWithVariantGift() {
        return service.getPromosWithVariantGift();
    }

    @GetMapping("/active")
    public List<PromotionProductDTO> getActivePromos() {
        return service.getActivePromos();
    }

    @GetMapping("/inactive")
    public List<PromotionProductDTO> getInactivePromos() {
        return service.getInactivePromos();
    }
}
