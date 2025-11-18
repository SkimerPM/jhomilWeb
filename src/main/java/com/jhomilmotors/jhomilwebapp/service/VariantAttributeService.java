package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.attribute.variant.VaAttributeRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.attribute.variant.VaAttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import com.jhomilmotors.jhomilwebapp.entity.VariantAttribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.AttributeRepository;
import com.jhomilmotors.jhomilwebapp.repository.ProductVariantRepository;
import com.jhomilmotors.jhomilwebapp.repository.VariantAttributeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VariantAttributeService {
    private final VariantAttributeRepository variantAttributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeRepository attributeRepository;

    public VariantAttributeService(VariantAttributeRepository variantAttributeRepository,  ProductVariantRepository productVariantRepository,  AttributeRepository attributeRepository) {
        this.variantAttributeRepository = variantAttributeRepository;
        this.productVariantRepository = productVariantRepository;
        this.attributeRepository = attributeRepository;
    }

    public Page<VaAttributeResponseDTO> searchByVarianteId(Long varianteId, Pageable pageable) {
        if (!productVariantRepository.existsById(varianteId)) {
            throw new ResourceNotFoundException("La variante no existe");
        }
        Page<VariantAttribute> variants = variantAttributeRepository.findByVarianteId(varianteId, pageable);
        return variants.map(this::toDTO);
    }

    public Page<VaAttributeResponseDTO> searchByVarianteIdAndAttributeTipo(Long varianteId, String tipo, Pageable pageable) {
        if (!productVariantRepository.existsById(varianteId)) {
            throw new ResourceNotFoundException("La variante no existe");
        }
        AttributeType tipoAt;
        try {
            tipoAt = AttributeType.fromValue(tipo);
        } catch (IllegalArgumentException ex) {
            throw new ResourceNotFoundException("Tipo de atributo inválido: " + tipo);
        }
        return variantAttributeRepository.findByVarianteIdAndAttribute_Tipo(varianteId, tipoAt, pageable).map(this::toDTO);
    }

    public Page<VaAttributeResponseDTO> searchByVarianteIdAndAttribute_Nombre(Long varianteId, String nombre, Pageable pageable) {
        if (!productVariantRepository.existsById(varianteId)) {
            throw new ResourceNotFoundException("La variante no existe");
        }
        return variantAttributeRepository
                .findByVarianteIdAndAttribute_NombreContainingIgnoreCase(varianteId, nombre.trim(), pageable)
                .map(this::toDTO);
    }

    public Page<VaAttributeResponseDTO> searchByAttributeId(Long attributeId, Pageable pageable) {
        return variantAttributeRepository.findByAttributeId(attributeId, pageable).map(this::toDTO);
    }

    public VaAttributeResponseDTO toDTO(VariantAttribute va) {
        return VaAttributeResponseDTO.builder()
                .id(va.getId())
                .varianteId(va.getVariante().getId())
                .atributoId(va.getAttribute().getId())
                .atributoNombre(va.getAttribute().getNombre())
                .atributoTipo(va.getAttribute().getTipo().toString())
                .atributoUnidad(va.getAttribute().getUnidad())
                .valorText(va.getValorText())
                .valorNum(va.getValorNum())
                .build();
    }

    public VaAttributeResponseDTO create(VaAttributeRequestDTO dto) {
        ProductVariant variante = productVariantRepository.findById(dto.getVarianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Variante no existe"));
        Attribute atributo = attributeRepository.findById(dto.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Atributo no existe"));

        if (variantAttributeRepository.findByVarianteIdAndAttributeId(dto.getVarianteId(), dto.getAttributeId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe ese atributo para la variante");
        }

        VariantAttribute va = new VariantAttribute();
        va.setVariante(variante);
        va.setAttribute(atributo);
        va.setValorText(dto.getValorText());
        va.setValorNum(dto.getValorNum());
        VariantAttribute saved = variantAttributeRepository.save(va);
        return toDTO(saved);
    }

    public VaAttributeResponseDTO update(Long id, VaAttributeRequestDTO dto) {
        VariantAttribute va = variantAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el atributo de variante"));
        va.setValorText(dto.getValorText());
        va.setValorNum(dto.getValorNum());
        VariantAttribute updated = variantAttributeRepository.save(va);
        return toDTO(updated);
    }

    public void delete(Long id) {
        if (!variantAttributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe el atributo de variante");
        }
        variantAttributeRepository.deleteById(id);
    }

}
