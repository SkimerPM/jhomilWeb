package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.attribute.normal.ProductAttributeRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.attribute.normal.ProductAttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/product-attributes")
@RequiredArgsConstructor
public class ProductAttributeAdminController {

    private final ProductAttributeService productAttributeService;

    /**
     * Lista todos los atributos de un producto principal (no variante), paginado.
     */
    @GetMapping("/by-product/{productId}")
    public Page<ProductAttributeResponseDTO> getByProduct(
            @PathVariable Long productId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productAttributeService.searchByProductId(productId, pageable);
    }

    /**
     * Lista atributos de un producto filtrando por tipo (ej: "texto", "decimal", etc.).
     */
    @GetMapping("/by-product/{productId}/tipo")
    public Page<ProductAttributeResponseDTO> getByProductAndType(
            @PathVariable Long productId,
            @RequestParam String tipo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productAttributeService.searchByProductIdAndAttributeTipo(productId, tipo, pageable);
    }

    /**
     * Lista atributos de un producto cuyo nombre contenga la cadena indicada.
     */
    @GetMapping("/by-product/{productId}/buscar-nombre")
    public Page<ProductAttributeResponseDTO> getByProductAndAttributeName(
            @PathVariable Long productId,
            @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productAttributeService.searchByProductIdAndAttribute_Nombre(productId, nombre, pageable);
    }

    /**
     * Lista relaciones de atributo-productos usando un atributo específico.
     */
    @GetMapping("/by-attribute/{attributeId}")
    public Page<ProductAttributeResponseDTO> getByAttribute(
            @PathVariable Long attributeId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productAttributeService.searchByAttributeId(attributeId, pageable);
    }

    /**
     * Crea un nuevo atributo asociado a un producto.
     */
    @PostMapping
    public ProductAttributeResponseDTO create(@RequestBody ProductAttributeRequestDTO dto) {
        return productAttributeService.create(dto);
    }

    /**
     * Edita valor del atributo del producto.
     */
    @PutMapping("/{id}")
    public ProductAttributeResponseDTO update(
            @PathVariable Long id,
            @RequestBody ProductAttributeRequestDTO dto
    ) {
        return productAttributeService.update(id, dto);
    }

    /**
     * Elimina el atributo asociado al producto.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productAttributeService.delete(id);
    }
}
