package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.attribute.normal.ProductAttributeRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.attribute.normal.ProductAttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductAttribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.AttributeRepository;
import com.jhomilmotors.jhomilwebapp.repository.ProductAttributeRepository;
import com.jhomilmotors.jhomilwebapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductAttributeService {

    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final ProductAttributeRepository productAttributeRepository;

    public Page<ProductAttributeResponseDTO> searchByProductId(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("El producto no existe");
        }
        return productAttributeRepository.findByProductId(productId, pageable)
                .map(this::toDTO);
    }

    public Page<ProductAttributeResponseDTO> searchByProductIdAndAttributeTipo(Long productId, String tipo, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("El producto no existe");
        }
        AttributeType tipoAt;
        try {
            tipoAt = AttributeType.fromValue(tipo);
        } catch (IllegalArgumentException ex) {
            throw new ResourceNotFoundException("Tipo de atributo inválido: " + tipo);
        }
        return productAttributeRepository.findByProductIdAndAttribute_Tipo(productId, tipoAt, pageable)
                .map(this::toDTO);
    }

    public Page<ProductAttributeResponseDTO> searchByProductIdAndAttribute_Nombre(Long productId, String nombre, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("El producto no existe");
        }
        return productAttributeRepository.findByProductIdAndAttribute_NombreContainingIgnoreCase(productId, nombre.trim(), pageable)
                .map(this::toDTO);
    }

    public Page<ProductAttributeResponseDTO> searchByAttributeId(Long attributeId, Pageable pageable) {
        return productAttributeRepository.findByAttributeId(attributeId, pageable)
                .map(this::toDTO);
    }

    public ProductAttributeResponseDTO create(ProductAttributeRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no existe"));
        Attribute attribute = attributeRepository.findById(dto.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Atributo no existe"));

        if (productAttributeRepository.findByProductIdAndAttributeId(dto.getProductId(), dto.getAttributeId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe ese atributo para el producto");
        }

        ProductAttribute pa = new ProductAttribute();
        pa.setProduct(product);
        pa.setAttribute(attribute);
        pa.setValorText(dto.getValorText());
        pa.setValorNum(dto.getValorNum());
        ProductAttribute saved = productAttributeRepository.save(pa);
        return toDTO(saved);
    }

    public ProductAttributeResponseDTO update(Long id, ProductAttributeRequestDTO dto) {
        ProductAttribute pa = productAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el atributo del producto"));
        pa.setValorText(dto.getValorText());
        pa.setValorNum(dto.getValorNum());
        ProductAttribute updated = productAttributeRepository.save(pa);
        return toDTO(updated);
    }

    public void delete(Long id) {
        if (!productAttributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe el atributo del producto");
        }
        productAttributeRepository.deleteById(id);
    }

    public ProductAttributeResponseDTO toDTO(ProductAttribute pa) {
        return ProductAttributeResponseDTO.builder()
                .id(pa.getId())
                .productId(pa.getProduct().getId())
                .attributeId(pa.getAttribute().getId())
                .attributeName(pa.getAttribute().getNombre())
                .attributeType(pa.getAttribute().getTipo().toString())
                .attributeUnidad(pa.getAttribute().getUnidad())
                .valorText(pa.getValorText())
                .valorNum(pa.getValorNum())
                .build();
    }
}
