package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.attribute.AttributeRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.attribute.AttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import com.jhomilmotors.jhomilwebapp.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttributeService {
    private final AttributeRepository attributeRepository;

    public Optional<Attribute> getByCodigo(String codigo) {
        return attributeRepository.findByCodigo(codigo);
    }

   public Page<AttributeResponseDTO> listAll(Pageable pageable) {
        return attributeRepository.findAll(pageable).map(this::toDTO);
   }

    public Page<AttributeResponseDTO> searchByNombre(String nombre, Pageable pageable) {
        return attributeRepository.findByNombreContainingIgnoreCase(nombre, pageable)
                .map(this::toDTO);
    }

   public AttributeResponseDTO create(AttributeRequestDTO dto) {
        Attribute at =  toEntity(dto);
        at = attributeRepository.save(at);
        return toDTO(at);
   }

    public AttributeResponseDTO update(Long id, AttributeRequestDTO dto) {
        Attribute att = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el Atributo " + id));

        att.setCodigo((dto.getCodigo() != null && !dto.getCodigo().trim().isEmpty()) ? dto.getCodigo() : att.getCodigo());
        att.setNombre((dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) ? dto.getNombre() : att.getNombre());
        att.setTipo((dto.getTipo() != null && !dto.getTipo().trim().isEmpty()) ? AttributeType.valueOf(dto.getTipo()) : att.getTipo());
        att.setUnidad((dto.getUnidad() != null && !dto.getUnidad().trim().isEmpty()) ? dto.getUnidad() : att.getUnidad());
        att.setEsVariacion(dto.getEsVariacion() != null ? dto.getEsVariacion() : att.getEsVariacion());
        att.setOrdenVisual(dto.getOrdenVisual() != null ? dto.getOrdenVisual() : att.getOrdenVisual());

        Attribute updated = attributeRepository.save(att);
        return toDTO(updated);
    }

    public void delete(Long id) {
        if (!attributeRepository.existsById(id)) throw new RuntimeException("No encontrado");
        attributeRepository.deleteById(id);
    }

    public List<Attribute> getAllOrdered() {
        return attributeRepository.findAllByOrderByOrdenVisualAsc();
    }

    private AttributeResponseDTO toDTO(Attribute attr) {
        return AttributeResponseDTO.builder()
                .id(attr.getId())
                .nombre(attr.getNombre())
                .codigo(attr.getCodigo())
                .tipo(attr.getTipo().toString())
                .unidad(attr.getUnidad())
                .esVariacion(attr.getEsVariacion())
                .ordenVisual(attr.getOrdenVisual())
                .build();
    }
    private Attribute toEntity(AttributeRequestDTO dto) {
        Attribute attr = new Attribute();
        attr.setNombre(dto.getNombre());
        attr.setCodigo(dto.getCodigo());
        attr.setTipo(AttributeType.valueOf(dto.getTipo()));
        attr.setUnidad(dto.getUnidad());
        attr.setEsVariacion(dto.getEsVariacion());
        attr.setOrdenVisual(dto.getOrdenVisual());
        return attr;
    }
}
