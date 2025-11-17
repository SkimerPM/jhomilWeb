package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.attribute.variant.VaAttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.service.VariantAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/variant-attributes")
@RequiredArgsConstructor
public class VariantAttributePublicController {

    private final VariantAttributeService variantAttributeService;

    // Listar atributos de una variante (para mostrar ficha en cliente)
    @GetMapping("/by-variant/{varianteId}")
    public Page<VaAttributeResponseDTO> getByVariant(
            @PathVariable Long varianteId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return variantAttributeService.searchByVarianteId(varianteId, pageable);
    }
}
