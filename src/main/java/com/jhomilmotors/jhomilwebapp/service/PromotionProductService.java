package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.PromotionProductDTO;
import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import com.jhomilmotors.jhomilwebapp.entity.Promotion;
import com.jhomilmotors.jhomilwebapp.entity.PromotionProduct;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.ProductRepository;
import com.jhomilmotors.jhomilwebapp.repository.ProductVariantRepository;
import com.jhomilmotors.jhomilwebapp.repository.PromotionProductRepository;
import com.jhomilmotors.jhomilwebapp.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionProductService {

    private final PromotionProductRepository promotionProductRepository;
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    // -------------------------------
    // DTO <-> Entity
    public PromotionProductDTO toDTO(PromotionProduct entity) {
        PromotionProductDTO dto = new PromotionProductDTO();
        dto.setId(entity.getId());
        dto.setPromotionId(entity.getPromocion() != null ? entity.getPromocion().getId() : null);
        dto.setProductId(entity.getProducto() != null ? entity.getProducto().getId() : null);
        dto.setVariantId(entity.getVariante() != null ? entity.getVariante().getId() : null);
        dto.setProductGratisId(entity.getProductoGratis() != null ? entity.getProductoGratis().getId() : null);
        dto.setVariantGratisId(entity.getVarianteGratis() != null ? entity.getVarianteGratis().getId() : null);
        dto.setCantidadRequerida(entity.getCantidadRequerida());
        dto.setCantidadGratis(entity.getCantidadGratis());
        return dto;
    }

    // -------------------------------
    // CRUD
    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getAll() {
        return promotionProductRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromotionProductDTO getById(Long id) {
        return promotionProductRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción-producto no encontrada con ID: " + id));
    }

    @Transactional
    public PromotionProductDTO create(PromotionProductDTO dto) {
        // Validaciones
        if (dto.getPromotionId() == null)
            throw new IllegalArgumentException("promotionId es obligatorio");
        if (dto.getCantidadRequerida() == null || dto.getCantidadRequerida() < 1)
            throw new IllegalArgumentException("cantidadRequerida debe ser mayor o igual a 1");

        Promotion promotion = promotionRepository.findById(dto.getPromotionId())
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));
        Product product = dto.getProductId() != null ?
                productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado")) : null;
        ProductVariant variant = dto.getVariantId() != null ?
                productVariantRepository.findById(dto.getVariantId())
                        .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada")) : null;
        Product productGratis = dto.getProductGratisId() != null ?
                productRepository.findById(dto.getProductGratisId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto de regalo no encontrado")) : null;
        ProductVariant variantGratis = dto.getVariantGratisId() != null ?
                productVariantRepository.findById(dto.getVariantGratisId())
                        .orElseThrow(() -> new ResourceNotFoundException("Variante de regalo no encontrada")) : null;

        PromotionProduct pp = new PromotionProduct();
        pp.setPromocion(promotion);
        pp.setProducto(product);
        pp.setVariante(variant);
        pp.setProductoGratis(productGratis);
        pp.setVarianteGratis(variantGratis);
        pp.setCantidadRequerida(dto.getCantidadRequerida());
        pp.setCantidadGratis(dto.getCantidadGratis() != null ? dto.getCantidadGratis() : 1);

        PromotionProduct saved = promotionProductRepository.save(pp);
        return toDTO(saved);
    }

    @Transactional
    public PromotionProductDTO update(Long id, PromotionProductDTO dto) {
        PromotionProduct entity = promotionProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción-producto no encontrada con ID: " + id));
        // Solo actualiza los campos enviados
        if (dto.getPromotionId() != null) {
            Promotion promo = promotionRepository.findById(dto.getPromotionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));
            entity.setPromocion(promo);
        }
        if (dto.getProductId() != null) {
            Product prod = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            entity.setProducto(prod);
        }
        if (dto.getVariantId() != null) {
            ProductVariant var = productVariantRepository.findById(dto.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
            entity.setVariante(var);
        }
        if (dto.getProductGratisId() != null) {
            Product prodFree = productRepository.findById(dto.getProductGratisId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto de regalo no encontrado"));
            entity.setProductoGratis(prodFree);
        }
        if (dto.getVariantGratisId() != null) {
            ProductVariant varFree = productVariantRepository.findById(dto.getVariantGratisId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante de regalo no encontrada"));
            entity.setVarianteGratis(varFree);
        }
        if (dto.getCantidadRequerida() != null) entity.setCantidadRequerida(dto.getCantidadRequerida());
        if (dto.getCantidadGratis() != null) entity.setCantidadGratis(dto.getCantidadGratis());

        PromotionProduct updated = promotionProductRepository.save(entity);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!promotionProductRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promoción-producto no encontrada con ID: " + id);
        }
        promotionProductRepository.deleteById(id);
    }



    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return promotionProductRepository.findByProducto(product).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getActiveByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return promotionProductRepository.findByProductoAndPromocionActivoTrue(product).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getInactiveByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return promotionProductRepository.findByProductoAndPromocionActivoFalse(product).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getByVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
        return promotionProductRepository.findByVariante(variant).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getActiveByVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
        return promotionProductRepository.findByVarianteAndPromocionActivoTrue(variant).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getInactiveByVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
        return promotionProductRepository.findByVarianteAndPromocionActivoFalse(variant).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getPromosByRequiredAmount(int cantidad) {
        return promotionProductRepository.findByCantidadRequeridaGreaterThan(cantidad)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getPromosWithProductGift() {
        return promotionProductRepository.findByProductoGratisIsNotNull().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getPromosWithVariantGift() {
        return promotionProductRepository.findByVarianteGratisIsNotNull().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getActivePromos() {
        return promotionProductRepository.findByPromocionActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getInactivePromos() {
        return promotionProductRepository.findByPromocionActivoFalse().stream().map(this::toDTO).collect(Collectors.toList());
    }
}
