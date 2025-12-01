package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.ProductOnSaleDTO;
import com.jhomilmotors.jhomilwebapp.dto.PromotionProductDTO;
import com.jhomilmotors.jhomilwebapp.entity.*;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

        // Si la promoción fue creada a nivel PRODUCTO (product != null) y no se especificó variante,
        // propagamos la promoción a las variantes activas del producto evitando duplicados.
        if (product != null && variant == null) {
            List<ProductVariant> variants = productVariantRepository.findByProductIdAndActivoTrue(product.getId());
            List<PromotionProduct> existingForPromo = promotionProductRepository.findByPromocionId(promotion.getId());

            for (ProductVariant v : variants) {
                boolean exists = existingForPromo.stream()
                        .anyMatch(ppExist -> ppExist.getVariante() != null && ppExist.getVariante().getId().equals(v.getId()));
                if (!exists) {
                    PromotionProduct vpp = new PromotionProduct();
                    vpp.setPromocion(promotion);
                    vpp.setProducto(product);
                    vpp.setVariante(v);
                    vpp.setProductoGratis(productGratis);
                    vpp.setVarianteGratis(variantGratis);
                    vpp.setCantidadRequerida(pp.getCantidadRequerida());
                    vpp.setCantidadGratis(pp.getCantidadGratis());
                    promotionProductRepository.save(vpp);
                }
            }
        }

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

    // ==========================================
    // NUEVA LÓGICA PARA APP MÓVIL (OFERTAS)
    // ==========================================

    @Transactional(readOnly = true)
    public Page<ProductOnSaleDTO> getProductsOnSale(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        // 1. Buscamos las entidades con la query optimizada
        Page<PromotionProduct> entities = promotionProductRepository.findActiveOnSaleProducts(now, pageable);

        // 2. Transformamos a DTO calculando precios
        return entities.map(this::mapToOnSaleDTO);
    }

    private ProductOnSaleDTO mapToOnSaleDTO(PromotionProduct pp) {
        Product product = pp.getProducto();
        ProductVariant variant = pp.getVariante();
        Promotion promo = pp.getPromocion();

        // A. Determinar Precio Base, Nombre y SKU
        BigDecimal originalPrice;
        String displayName;
        String sku;
        Long variantId = null;

        // Si la oferta es a una variante específica, usamos sus datos.
        // Si no, usamos los del producto base.
        if (variant != null) {
            originalPrice = variant.getPrecio();
            displayName = product.getNombre(); // Podrías concatenar atributos si quisieras
            sku = variant.getSku();
            variantId = variant.getId();
        } else {
            originalPrice = product.getPrecioBase();
            displayName = product.getNombre();
            sku = product.getSkuBase();
        }

        // Evitar NullPointer en precio
        if (originalPrice == null) originalPrice = BigDecimal.ZERO;

        // B. Calcular Descuento y Precio Final
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Switch basado en tu Enum DiscountType
        switch (promo.getTipoDescuento()) {
            case PORCENTAJE:
                // Cálculo: Precio * (Porcentaje / 100)
                discountAmount = originalPrice.multiply(promo.getValorDescuento())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
            case MONTO_FIJO:
                // Cálculo: Descuento directo
                discountAmount = promo.getValorDescuento();
                break;
            case DOS_POR_UNO:
                // En 2x1, el precio unitario se muestra igual, la oferta es llevarse otro.
                // Opcional: Si quieres mostrar precio efectivo, descomenta abajo.
                // discountAmount = originalPrice.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                discountAmount = BigDecimal.ZERO;
                break;
        }

        // Precio final = Original - Descuento (Mínimo 0)
        BigDecimal finalPrice = originalPrice.subtract(discountAmount).max(BigDecimal.ZERO);

        // C. Obtener Imagen Principal
        // Prioridad: Imagen de Variante > Imagen de Producto
        String imageUrl = null;
        List<Image> imagesToCheck = (variant != null && variant.getImagenes() != null && !variant.getImagenes().isEmpty())
                ? variant.getImagenes()
                : product.getImagenes();

        if (imagesToCheck != null && !imagesToCheck.isEmpty()) {
            imageUrl = imagesToCheck.stream()
                    .filter(img -> Boolean.TRUE.equals(img.getEsPrincipal()))
                    .findFirst()
                    .map(Image::getUrl)
                    .orElse(imagesToCheck.get(0).getUrl()); // Fallback a la primera imagen
        }

        // D. Construir DTO
        return ProductOnSaleDTO.builder()
                .promotionProductId(pp.getId())
                .productId(product.getId())
                .variantId(variantId)
                .productName(displayName)
                .sku(sku)
                .imageUrl(imageUrl)
                .promotionLabel(promo.getNombre())
                .discountType(promo.getTipoDescuento().getValue()) // Usamos .getValue() del Enum
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }

}