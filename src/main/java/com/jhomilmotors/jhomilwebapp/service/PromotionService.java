package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.PromotionDTO;
import com.jhomilmotors.jhomilwebapp.entity.Promotion;
import com.jhomilmotors.jhomilwebapp.enums.DiscountType;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    private DiscountType strToDiscountType(String value) {
        for (DiscountType tipo : DiscountType.values()) {
            if (tipo.getValue().equalsIgnoreCase(value.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de descuento inválido: " + value);
    }

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
        return dto;
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> getAll() {
        return promotionRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromotionDTO getById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con ID: " + id));
        return toDTO(promotion);
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
    public PromotionDTO getByCodigo(String code) {
        Promotion promo = promotionRepository.findByCodigo(code)
                .orElseThrow(() -> new ResourceNotFoundException("No existe promoción con código: " + code));
        return toDTO(promo);
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

    @Transactional
    public PromotionDTO create(PromotionDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la promoción es obligatorio");
        if (dto.getTipoDescuento() == null || dto.getTipoDescuento().isBlank())
            throw new IllegalArgumentException("El tipo de descuento es obligatorio");
        if (dto.getCodigo() != null && promotionRepository.findByCodigo(dto.getCodigo()).isPresent())
            throw new IllegalArgumentException("El código de promoción ya existe");

        DiscountType tipoDescuento = strToDiscountType(dto.getTipoDescuento());
        if ((tipoDescuento == DiscountType.PORCENTAJE || tipoDescuento == DiscountType.MONTO_FIJO)
                && (dto.getValorDescuento() == null || dto.getValorDescuento().compareTo(BigDecimal.ZERO) <= 0))
            throw new IllegalArgumentException("El valor de descuento debe ser mayor a cero para porcentaje o monto fijo");

        if (dto.getFechaFin() != null && dto.getFechaInicio() != null &&
                dto.getFechaFin().isBefore(dto.getFechaInicio()))
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha inicio");

        Promotion promotion = new Promotion();
        promotion.setNombre(dto.getNombre());
        promotion.setCodigo(dto.getCodigo());
        promotion.setTipoDescuento(tipoDescuento);
        promotion.setValorDescuento(dto.getValorDescuento() != null ? dto.getValorDescuento() : BigDecimal.ZERO);
        promotion.setFechaInicio(dto.getFechaInicio() != null ? dto.getFechaInicio() : LocalDateTime.now());
        promotion.setFechaFin(dto.getFechaFin());
        promotion.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        promotion.setMinCompra(dto.getMinCompra());
        promotion.setMaxUsos(dto.getMaxUsos());

        Promotion saved = promotionRepository.save(promotion);
        return toDTO(saved);
    }

    @Transactional
    public PromotionDTO update(Long id, PromotionDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con ID: " + id));
        if (dto.getNombre() != null) promotion.setNombre(dto.getNombre());
        if (dto.getCodigo() != null) promotion.setCodigo(dto.getCodigo());
        if (dto.getTipoDescuento() != null) promotion.setTipoDescuento(strToDiscountType(dto.getTipoDescuento()));
        if (dto.getValorDescuento() != null) promotion.setValorDescuento(dto.getValorDescuento());
        if (dto.getFechaInicio() != null) promotion.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) promotion.setFechaFin(dto.getFechaFin());
        if (dto.getActivo() != null) promotion.setActivo(dto.getActivo());
        if (dto.getMinCompra() != null) promotion.setMinCompra(dto.getMinCompra());
        if (dto.getMaxUsos() != null) promotion.setMaxUsos(dto.getMaxUsos());

        Promotion updated = promotionRepository.save(promotion);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promoción no encontrada con ID: " + id);
        }
        promotionRepository.deleteById(id);
    }
}
