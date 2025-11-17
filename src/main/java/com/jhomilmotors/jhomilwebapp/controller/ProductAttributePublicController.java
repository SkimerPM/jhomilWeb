package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.attribute.normal.ProductAttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/product-attributes")
@RequiredArgsConstructor
public class ProductAttributePublicController {

    private final ProductAttributeService productAttributeService;

    // Listar atributos de un producto (para mostrar ficha en cliente)
    @GetMapping("/by-product/{productId}")
    public Page<ProductAttributeResponseDTO> getByProduct(
            @PathVariable Long productId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productAttributeService.searchByProductId(productId, pageable);
    }
}