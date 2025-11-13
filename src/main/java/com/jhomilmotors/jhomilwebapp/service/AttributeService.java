package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import com.jhomilmotors.jhomilwebapp.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttributeService {
    private final AttributeRepository attributeRepository;

    public Optional<Attribute> getByCodigo(String codigo) {
        return attributeRepository.findByCodigo(codigo);
    }

    public List<Attribute> getByTipo(AttributeType tipo) {
        return attributeRepository.findByTipo(tipo);
    }

    public List<Attribute> getAllVariationAttributes() {
        return attributeRepository.findByEsVariacionTrue();
    }

    public List<Attribute> searchByNombre(String nombre) {
        return attributeRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Attribute> getAllOrdered() {
        return attributeRepository.findAllByOrderByOrdenVisualAsc();
    }
}
