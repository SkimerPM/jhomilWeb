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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    // DTO <-> Entity (Método de mapeo)
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

    // -----------------------------------------------------------
    // CRUD (Manteniendo la versión List<DTO> para compatibilidad)
    // -----------------------------------------------------------

    // NOTA: Reemplazamos getAll() por la versión paginada: getAll(Pageable pageable)
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

    // -----------------------------------------------------------
    // Consultas Paginadas (Page<DTO>)
    // -----------------------------------------------------------

    /**
     * 1. Obtiene una página de todas las configuraciones Promoción-Producto.
     */
    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getAll(Pageable pageable) {
        return promotionProductRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 2. Obtiene una página de configuraciones asociadas a un promotionId específico.
     */
    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getProductsByPromotionId(Long promotionId, Pageable pageable) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con ID: " + promotionId));

        // Usa el método findByPromocion que definimos en el Repository
        return promotionProductRepository.findByPromocion(promotion, pageable).map(this::toDTO);
    }

    // -----------------------------
    // Por producto (PAGINADO)
    // -----------------------------
    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getByProduct(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return promotionProductRepository.findByProducto(product, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getActiveByProduct(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return promotionProductRepository.findByProductoAndPromocionActivoTrue(product, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getInactiveByProduct(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return promotionProductRepository.findByProductoAndPromocionActivoFalse(product, pageable).map(this::toDTO);
    }

    // -----------------------------
    // Por variante (PAGINADO)
    // -----------------------------
    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getByVariant(Long variantId, Pageable pageable) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
        return promotionProductRepository.findByVariante(variant, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getActiveByVariant(Long variantId, Pageable pageable) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
        return promotionProductRepository.findByVarianteAndPromocionActivoTrue(variant, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getInactiveByVariant(Long variantId, Pageable pageable) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
        return promotionProductRepository.findByVarianteAndPromocionActivoFalse(variant, pageable).map(this::toDTO);
    }

    // -----------------------------
    // Otros filtros (PAGINADO)
    // -----------------------------

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getPromosByRequiredAmount(int cantidad, Pageable pageable) {
        return promotionProductRepository.findByCantidadRequeridaGreaterThan(cantidad, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getPromosWithProductGift(Pageable pageable) {
        return promotionProductRepository.findByProductoGratisIsNotNull(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getPromosWithVariantGift(Pageable pageable) {
        return promotionProductRepository.findByVarianteGratisIsNotNull(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getActivePromos(Pageable pageable) {
        return promotionProductRepository.findByPromocionActivoTrue(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getInactivePromos(Pageable pageable) {
        return promotionProductRepository.findByPromocionActivoFalse(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PromotionProductDTO> getByProductNameContaining(String nombreProducto, Pageable pageable) {
        Page<PromotionProduct> entitiesPage =
                promotionProductRepository.findByProductoNombreContaining(nombreProducto, pageable);
        return entitiesPage.map(this::toDTO);
    }

    // Obtiene promociones por id de producto
    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getPromotionsByProductId(Long productId) {
        return promotionProductRepository.findByProductoId(productId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Obtiene promociones por id de variante
    @Transactional(readOnly = true)
    public List<PromotionProductDTO> getPromotionsByVariantId(Long variantId) {
        return promotionProductRepository.findByVarianteId(variantId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

}