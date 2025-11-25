package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.PromotionDTO;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import com.jhomilmotors.jhomilwebapp.entity.Promotion;
import com.jhomilmotors.jhomilwebapp.entity.PromotionProduct;
import com.jhomilmotors.jhomilwebapp.enums.DiscountType;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.ProductVariantRepository;
import com.jhomilmotors.jhomilwebapp.repository.PromotionProductRepository;
import com.jhomilmotors.jhomilwebapp.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductVariantRepository productVariantRepository;

    // -------------------------------------------------------------------------
    // 🔍 MÉTODOS DE CONVERSIÓN Y LECTURA (DTO)
    // -------------------------------------------------------------------------

    public PromotionDTO toDTO(Promotion entity) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCodigo(entity.getCodigo());
        dto.setTipoDescuento(entity.getTipoDescuento().getValue());
        dto.setValorDescuento(entity.getValorDescuento());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setActivo(entity.getActivo());
        dto.setMinCompra(entity.getMinCompra());
        dto.setMaxUsos(entity.getMaxUsos());

        // 🔍 LÓGICA DE LECTURA DE REGALOS
        // Buscamos si esta promoción tiene configurado un regalo en la tabla hija
        List<PromotionProduct> reglas = promotionProductRepository.findByPromocionId(entity.getId());

        // Buscamos la primera regla que tenga un varianteGratis configurado
        Optional<PromotionProduct> reglaConRegalo = reglas.stream()
                .filter(pp -> pp.getVarianteGratis() != null)
                .findFirst();

        if (reglaConRegalo.isPresent()) {
            dto.setVarianteGratisId(reglaConRegalo.get().getVarianteGratis().getId());
            dto.setCantidadGratis(reglaConRegalo.get().getCantidadGratis() != null ? reglaConRegalo.get().getCantidadGratis() : 1);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public PromotionDTO getByCodigo(String code) {
        Promotion promo = promotionRepository.findByCodigo(code)
                .orElseThrow(() -> new ResourceNotFoundException("No existe promoción con código: " + code));
        return toDTO(promo);
    }

    @Transactional(readOnly = true)
    public PromotionDTO getById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con ID: " + id));
        return toDTO(promotion);
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> getAll() {
        return promotionRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> getActive() {
        return promotionRepository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> getByTipoDescuento(String tipo) {
        DiscountType discountType = strToDiscountType(tipo);
        return promotionRepository.findByTipoDescuento(discountType).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> getActiveByTipoDescuento(String tipo) {
        DiscountType discountType = strToDiscountType(tipo);
        return promotionRepository.findByActivoTrueAndTipoDescuento(discountType).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> getVigentes() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByFechaInicioBeforeAndFechaFinAfter(now, now)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> getActivasVigentes() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByActivoTrueAndFechaInicioBeforeAndFechaFinAfter(now, now)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // 🧠 LÓGICA ESPECIAL: MEJOR PROMOCIÓN POR VARIANTE
    // -------------------------------------------------------------------------

    /**
     * Busca la mejor promoción activa para una variante:
     * 1. Busca promociones específicas de la variante.
     * 2. Si no hay, busca promociones del producto padre.
     */
    @Transactional(readOnly = true)
    public PromotionDTO findBestActivePromotionByVariantId(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada."));

        LocalDateTime now = LocalDateTime.now();

        // 1. Buscar promociones específicas de la VARIANTE (más específicas)
        List<PromotionProduct> promoVariantLinks = promotionProductRepository.findByVarianteId(variantId);

        Optional<PromotionDTO> bestVariantPromo = promoVariantLinks.stream()
                .map(PromotionProduct::getPromocion)
                .filter(p -> p.getActivo()
                        && p.getFechaInicio().isBefore(now)
                        && (p.getFechaFin() == null || p.getFechaFin().isAfter(now)))
                .map(this::toDTO)
                .findFirst();

        if (bestVariantPromo.isPresent()) {
            return bestVariantPromo.get();
        }

        // 2. Si no hay, buscar promociones del PRODUCTO PADRE
        List<PromotionProduct> promoProductLinks = promotionProductRepository.findByProductoId(variant.getProduct().getId());

        Optional<PromotionDTO> bestProductPromo = promoProductLinks.stream()
                .map(PromotionProduct::getPromocion)
                .filter(p -> p.getActivo()
                        && p.getFechaInicio().isBefore(now)
                        && (p.getFechaFin() == null || p.getFechaFin().isAfter(now)))
                .map(this::toDTO)
                .findFirst();

        return bestProductPromo.orElse(null);
    }

    // -------------------------------------------------------------------------
    // 💾 MÉTODOS DE ESCRITURA (CREAR/ACTUALIZAR/BORRAR)
    // -------------------------------------------------------------------------

    @Transactional
    public PromotionDTO create(PromotionDTO dto) {
        validarDto(dto);

        Promotion promotion = new Promotion();
        mapDtoToEntity(dto, promotion);

        // 1. Guardamos la Promoción Padre
        Promotion savedPromo = promotionRepository.save(promotion);

        // 2. 🎁 LÓGICA DE GUARDADO DE REGALO
        if (dto.getVarianteGratisId() != null) {
            createGiftRule(savedPromo, dto.getVarianteGratisId(), dto.getCantidadGratis());
        }

        return toDTO(savedPromo);
    }

    @Transactional
    public PromotionDTO update(Long id, PromotionDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con ID: " + id));

        mapDtoToEntity(dto, promotion);
        Promotion updatedPromo = promotionRepository.save(promotion);

        // 3. 🎁 LÓGICA DE ACTUALIZACIÓN DE REGALO
        // Si el DTO menciona un regalo (o null para quitarlo), actualizamos la tabla hija
        updateGiftRule(updatedPromo, dto.getVarianteGratisId(), dto.getCantidadGratis());

        return toDTO(updatedPromo);
    }

    @Transactional
    public void delete(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promoción no encontrada con ID: " + id);
        }
        promotionRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // 🎁 MÉTODOS PRIVADOS DE GESTIÓN DE REGALOS
    // -------------------------------------------------------------------------

    private void createGiftRule(Promotion promotion, Long varianteGratisId, Integer cantidad) {
        ProductVariant variant = productVariantRepository.findById(varianteGratisId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de regalo no encontrada ID: " + varianteGratisId));

        PromotionProduct link = new PromotionProduct();
        link.setPromocion(promotion);
        link.setVarianteGratis(variant);
        link.setCantidadGratis(cantidad != null ? cantidad : 1);
        // Cantidad requerida por defecto 1 (o podrías agregarlo al DTO si la lógica cambia)
        link.setCantidadRequerida(1);

        promotionProductRepository.save(link);
    }

    private void updateGiftRule(Promotion promotion, Long nuevoVarianteGratisId, Integer nuevaCantidad) {
        // Obtenemos las reglas existentes
        List<PromotionProduct> reglas = promotionProductRepository.findByPromocionId(promotion.getId());

        // Buscamos si ya existía una regla de regalo
        Optional<PromotionProduct> reglaRegaloExistente = reglas.stream()
                .filter(pp -> pp.getVarianteGratis() != null)
                .findFirst();

        if (nuevoVarianteGratisId != null) {
            // CASO A: Queremos poner/cambiar un regalo
            if (reglaRegaloExistente.isPresent()) {
                // Actualizamos la existente
                PromotionProduct pp = reglaRegaloExistente.get();
                if (!pp.getVarianteGratis().getId().equals(nuevoVarianteGratisId)) {
                    ProductVariant nuevaVariante = productVariantRepository.findById(nuevoVarianteGratisId)
                            .orElseThrow(() -> new ResourceNotFoundException("Variante de regalo no encontrada"));
                    pp.setVarianteGratis(nuevaVariante);
                }
                pp.setCantidadGratis(nuevaCantidad != null ? nuevaCantidad : 1);
                promotionProductRepository.save(pp);
            } else {
                // Creamos una nueva
                createGiftRule(promotion, nuevoVarianteGratisId, nuevaCantidad);
            }
        } else {
            // CASO B: El DTO envió null, significa que queremos quitar el regalo si existía
            reglaRegaloExistente.ifPresent(promotionProductRepository::delete);
        }
    }

    // -------------------------------------------------------------------------
    // 🛡️ VALIDACIONES Y MAPEO AUXILIAR
    // -------------------------------------------------------------------------

    private DiscountType strToDiscountType(String value) {
        for (DiscountType tipo : DiscountType.values()) {
            if (tipo.getValue().equalsIgnoreCase(value.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de descuento inválido: " + value);
    }

    private void validarDto(PromotionDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (dto.getTipoDescuento() == null)
            throw new IllegalArgumentException("El tipo de descuento es obligatorio");

        // Validación de duplicado de código solo al crear (o manejar lógica ID en update)
        if (dto.getId() == null && dto.getCodigo() != null && promotionRepository.findByCodigo(dto.getCodigo()).isPresent())
            throw new IllegalArgumentException("El código de promoción ya existe");

        DiscountType tipoDescuento = strToDiscountType(dto.getTipoDescuento());
        if ((tipoDescuento == DiscountType.PORCENTAJE || tipoDescuento == DiscountType.MONTO_FIJO)
                && (dto.getValorDescuento() == null || dto.getValorDescuento().compareTo(BigDecimal.ZERO) <= 0))
            throw new IllegalArgumentException("El valor de descuento debe ser mayor a cero");

        if (dto.getFechaFin() != null && dto.getFechaInicio() != null &&
                dto.getFechaFin().isBefore(dto.getFechaInicio()))
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha inicio");
    }

    private void mapDtoToEntity(PromotionDTO dto, Promotion promotion) {
        if (dto.getNombre() != null) promotion.setNombre(dto.getNombre());
        if (dto.getCodigo() != null) promotion.setCodigo(dto.getCodigo());
        if (dto.getTipoDescuento() != null) promotion.setTipoDescuento(strToDiscountType(dto.getTipoDescuento()));
        if (dto.getValorDescuento() != null) promotion.setValorDescuento(dto.getValorDescuento());
        if (dto.getFechaInicio() != null) promotion.setFechaInicio(dto.getFechaInicio());
        else if (promotion.getFechaInicio() == null) promotion.setFechaInicio(LocalDateTime.now());

        if (dto.getFechaFin() != null) promotion.setFechaFin(dto.getFechaFin());
        if (dto.getActivo() != null) promotion.setActivo(dto.getActivo());
        else if (promotion.getActivo() == null) promotion.setActivo(true);

        if (dto.getMinCompra() != null) promotion.setMinCompra(dto.getMinCompra());
        if (dto.getMaxUsos() != null) promotion.setMaxUsos(dto.getMaxUsos());
    }
}