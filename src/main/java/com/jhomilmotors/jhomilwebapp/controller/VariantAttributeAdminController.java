package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.attribute.variant.VaAttributeRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.attribute.variant.VaAttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.service.VariantAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/variant-attributes")
@RequiredArgsConstructor
public class VariantAttributeAdminController {

    private final VariantAttributeService variantAttributeService;

    /**
     * Lista todos los atributos de una variante específica con paginación.
     * Ejemplo de uso: al editar una variante, mostrar sus atributos asociados.
     */
    @GetMapping("/by-variant/{varianteId}")
    public Page<VaAttributeResponseDTO> getByVariant(
            @PathVariable Long varianteId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return variantAttributeService.searchByVarianteId(varianteId, pageable);
    }

    /**
     * Lista los atributos de una variante filtrando solo por tipo (ej: "texto", "decimal", etc.).
     * Ejemplo: mostrar solo atributos numéricos de una variante.
     */
    @GetMapping("/by-variant/{varianteId}/tipo")
    public Page<VaAttributeResponseDTO> getByVariantAndType(
            @PathVariable Long varianteId,
            @RequestParam String tipo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return variantAttributeService.searchByVarianteIdAndAttributeTipo(varianteId, tipo, pageable);
    }

    /**
     * Lista los atributos de una variante cuyo nombre contenga la cadena indicada.
     * Caso de uso: buscar rápido entre los atributos de una variante (ej: buscar todos los de nombre "color").
     */
    @GetMapping("/by-variant/{varianteId}/buscar-nombre")
    public Page<VaAttributeResponseDTO> getByVariantAndAttributeName(
            @PathVariable Long varianteId,
            @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return variantAttributeService.searchByVarianteIdAndAttribute_Nombre(varianteId, nombre, pageable);
    }

    /**
     * Lista las relaciones de atributo-variantes usando un atributo específico.
     * Caso de uso: encontrar todas las variantes que tienen, por ejemplo, el atribut "color".
     */
    @GetMapping("/by-attribute/{attributeId}")
    public Page<VaAttributeResponseDTO> getByAttribute(
            @PathVariable Long attributeId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return variantAttributeService.searchByAttributeId(attributeId, pageable);
    }


    /**
     * Crea un nuevo atributo asociado a una variante.
     * Ejemplo de uso: En el admin, al agregar "Talla: XL" a una variante específica.
     */
    @PostMapping
    public VaAttributeResponseDTO create(@RequestBody VaAttributeRequestDTO dto) {
        return variantAttributeService.create(dto);
    }

    /**
     * Edita el valor de un atributo de variante existente.
     * Ejemplo de uso: Cambiar de "Color: Rojo" a "Color: Azul" para una variante.
     */
    @PutMapping("/{id}")
    public VaAttributeResponseDTO update(
            @PathVariable Long id,
            @RequestBody VaAttributeRequestDTO dto
    ) {
        return variantAttributeService.update(id, dto);
    }

    /**
     * Elimina el atributo asociado a una variante por su id.
     * Ejemplo de uso: Quitar la compatibilidad con "Toyota" de una variante.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        variantAttributeService.delete(id);
    }


}
