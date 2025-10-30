package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CreateSupplierDTO;
import com.jhomilmotors.jhomilwebapp.dto.SupplierDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateSupplierDTO;
import com.jhomilmotors.jhomilwebapp.entity.Supplier;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    /**
     * Obtiene todos los proveedores
     */
    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un proveedor por ID
     */
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        return convertToDTO(supplier);
    }

    /**
     * Busca proveedores por nombre
     */
    @Transactional(readOnly = true)
    public List<SupplierDTO> searchSuppliersByName(String nombre) {
        return supplierRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo proveedor
     */
    @Transactional
    public SupplierDTO createSupplier(CreateSupplierDTO createDTO) {
        // Validar que el RUC no exista si se proporciona
        if (createDTO.getRuc() != null && !createDTO.getRuc().isEmpty()) {
            if (supplierRepository.existsByRuc(createDTO.getRuc())) {
                throw new IllegalArgumentException("Ya existe un proveedor con el RUC: " + createDTO.getRuc());
            }
        }

        // Validar que el email no exista si se proporciona
        if (createDTO.getEmail() != null && !createDTO.getEmail().isEmpty()) {
            if (supplierRepository.existsByEmail(createDTO.getEmail())) {
                throw new IllegalArgumentException("Ya existe un proveedor con el email: " + createDTO.getEmail());
            }
        }

        Supplier supplier = Supplier.builder()
                .nombre(createDTO.getNombre())
                .ruc(createDTO.getRuc())
                .contacto(createDTO.getContacto())
                .telefono(createDTO.getTelefono())
                .email(createDTO.getEmail())
                .direccion(createDTO.getDireccion())
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToDTO(savedSupplier);
    }

    /**
     * Actualiza un proveedor existente
     */
    @Transactional
    public SupplierDTO updateSupplier(Long id, UpdateSupplierDTO updateDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        // Actualizar solo los campos que no sean nulos
        if (updateDTO.getNombre() != null) {
            supplier.setNombre(updateDTO.getNombre());
        }
        if (updateDTO.getRuc() != null) {
            // Validar que el RUC no esté en uso por otro proveedor
            if (supplierRepository.existsByRuc(updateDTO.getRuc())) {
                Supplier existingSupplier = supplierRepository.findByRuc(updateDTO.getRuc())
                        .orElse(null);
                if (existingSupplier != null && !existingSupplier.getId().equals(id)) {
                    throw new IllegalArgumentException("El RUC ya está en uso por otro proveedor");
                }
            }
            supplier.setRuc(updateDTO.getRuc());
        }
        if (updateDTO.getContacto() != null) {
            supplier.setContacto(updateDTO.getContacto());
        }
        if (updateDTO.getTelefono() != null) {
            supplier.setTelefono(updateDTO.getTelefono());
        }
        if (updateDTO.getEmail() != null) {
            // Validar que el email no esté en uso por otro proveedor
            if (supplierRepository.existsByEmail(updateDTO.getEmail())) {
                Supplier existingSupplier = supplierRepository.findByRuc(updateDTO.getEmail())
                        .orElse(null);
                if (existingSupplier != null && !existingSupplier.getId().equals(id)) {
                    throw new IllegalArgumentException("El email ya está en uso por otro proveedor");
                }
            }
            supplier.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getDireccion() != null) {
            supplier.setDireccion(updateDTO.getDireccion());
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return convertToDTO(updatedSupplier);
    }

    /**
     * Elimina un proveedor
     */
    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
        }
        supplierRepository.deleteById(id);
    }

    /**
     * Convierte una entidad Supplier a DTO
     */
    private SupplierDTO convertToDTO(Supplier supplier) {
        return SupplierDTO.builder()
                .id(supplier.getId())
                .nombre(supplier.getNombre())
                .ruc(supplier.getRuc())
                .contacto(supplier.getContacto())
                .telefono(supplier.getTelefono())
                .email(supplier.getEmail())
                .direccion(supplier.getDireccion())
                .build();
    }
}
