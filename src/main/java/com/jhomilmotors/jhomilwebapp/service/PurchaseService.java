package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.entity.Purchase.PurchaseStatus;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    /**
     * Obtiene todas las compras
     */
    @Transactional(readOnly = true)
    public List<PurchaseDTO> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una compra por ID
     */
    @Transactional(readOnly = true)
    public PurchaseDTO getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + id));
        return convertToDTO(purchase);
    }

    /**
     * Obtiene compras por proveedor
     */
    @Transactional(readOnly = true)
    public List<PurchaseDTO> getPurchasesBySupplier(Long supplierId) {
        return purchaseRepository.findByProveedorId(supplierId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene compras por estado
     */
    @Transactional(readOnly = true)
    public List<PurchaseDTO> getPurchasesByStatus(PurchaseStatus status) {
        return purchaseRepository.findByEstado(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva compra con sus items
     */
    @Transactional
    public PurchaseDTO createPurchase(CreatePurchaseDTO createDTO) {
        // Validar que el proveedor existe
        Supplier supplier = supplierRepository.findById(createDTO.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + createDTO.getProveedorId()));

        // Validar que el código no existe si se proporciona
        if (createDTO.getCodigo() != null && !createDTO.getCodigo().isEmpty()) {
            if (purchaseRepository.existsByCodigo(createDTO.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una compra con el código: " + createDTO.getCodigo());
            }
        }

        // Crear la compra
        Purchase purchase = Purchase.builder()
                .proveedor(supplier)
                .codigo(createDTO.getCodigo())
                .fechaCompra(createDTO.getFechaCompra() != null ? createDTO.getFechaCompra() : LocalDateTime.now())
                .subtotal(createDTO.getSubtotal())
                .impuestos(createDTO.getImpuestos())
                .total(createDTO.getTotal())
                .estado(PurchaseStatus.pendiente)
                .nota(createDTO.getNota())
                .items(new ArrayList<>())
                .build();

        Purchase savedPurchase = purchaseRepository.save(purchase);

        // Crear los items de la compra
        List<PurchaseItem> items = new ArrayList<>();
        for (CreatePurchaseItemDTO itemDTO : createDTO.getItems()) {
            // Validar que el producto existe
            Product product = productRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemDTO.getProductoId()));

            // Validar variante si se proporciona
            ProductVariant variant = null;
            if (itemDTO.getVarianteId() != null) {
                variant = productVariantRepository.findById(itemDTO.getVarianteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con ID: " + itemDTO.getVarianteId()));
            }

            PurchaseItem item = PurchaseItem.builder()
                    .compra(savedPurchase)
                    .producto(product)
                    .variante(variant)
                    .presentacion(itemDTO.getPresentacion())
                    .unidadesPorPresentacion(itemDTO.getUnidadesPorPresentacion())
                    .cantidadPresentaciones(itemDTO.getCantidadPresentaciones())
                    .cantidadUnidades(itemDTO.getCantidadUnidades())
                    .precioUnitarioPresentacion(itemDTO.getPrecioUnitarioPresentacion())
                    .precioUnitarioUnidad(itemDTO.getPrecioUnitarioUnidad())
                    .subtotal(itemDTO.getSubtotal())
                    .build();

            items.add(item);
        }

        List<PurchaseItem> savedItems = purchaseItemRepository.saveAll(items);
        savedPurchase.setItems(savedItems);

        return convertToDTO(savedPurchase);
    }

    /**
     * Actualiza una compra existente
     */
    @Transactional
    public PurchaseDTO updatePurchase(Long id, UpdatePurchaseDTO updateDTO) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + id));

        // Actualizar solo los campos que no sean nulos
        if (updateDTO.getCodigo() != null) {
            // Validar que el código no esté en uso por otra compra
            if (purchaseRepository.existsByCodigo(updateDTO.getCodigo())) {
                Purchase existingPurchase = purchaseRepository.findByCodigo(updateDTO.getCodigo())
                        .orElse(null);
                if (existingPurchase != null && !existingPurchase.getId().equals(id)) {
                    throw new IllegalArgumentException("El código ya está en uso por otra compra");
                }
            }
            purchase.setCodigo(updateDTO.getCodigo());
        }
        if (updateDTO.getSubtotal() != null) {
            purchase.setSubtotal(updateDTO.getSubtotal());
        }
        if (updateDTO.getImpuestos() != null) {
            purchase.setImpuestos(updateDTO.getImpuestos());
        }
        if (updateDTO.getTotal() != null) {
            purchase.setTotal(updateDTO.getTotal());
        }
        if (updateDTO.getEstado() != null) {
            purchase.setEstado(updateDTO.getEstado());
        }
        if (updateDTO.getNota() != null) {
            purchase.setNota(updateDTO.getNota());
        }

        Purchase updatedPurchase = purchaseRepository.save(purchase);
        return convertToDTO(updatedPurchase);
    }

    /**
     * Cambia el estado de una compra
     */
    @Transactional
    public PurchaseDTO updatePurchaseStatus(Long id, PurchaseStatus newStatus) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + id));

        purchase.setEstado(newStatus);
        Purchase updatedPurchase = purchaseRepository.save(purchase);
        return convertToDTO(updatedPurchase);
    }

    /**
     * Elimina una compra
     */
    @Transactional
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + id));

        // Eliminar primero los items
        purchaseItemRepository.deleteAll(purchase.getItems());

        // Eliminar la compra
        purchaseRepository.delete(purchase);
    }

    /**
     * Convierte una entidad Purchase a DTO
     */
    private PurchaseDTO convertToDTO(Purchase purchase) {
        List<PurchaseItemDTO> itemDTOs = purchase.getItems() != null ?
                purchase.getItems().stream()
                        .map(this::convertItemToDTO)
                        .collect(Collectors.toList()) : new ArrayList<>();

        return PurchaseDTO.builder()
                .id(purchase.getId())
                .proveedorId(purchase.getProveedor().getId())
                .proveedorNombre(purchase.getProveedor().getNombre())
                .codigo(purchase.getCodigo())
                .fechaCompra(purchase.getFechaCompra())
                .subtotal(purchase.getSubtotal())
                .impuestos(purchase.getImpuestos())
                .total(purchase.getTotal())
                .estado(purchase.getEstado())
                .nota(purchase.getNota())
                .items(itemDTOs)
                .build();
    }

    /**
     * Convierte una entidad PurchaseItem a DTO
     */
    private PurchaseItemDTO convertItemToDTO(PurchaseItem item) {
        return PurchaseItemDTO.builder()
                .id(item.getId())
                .productoId(item.getProducto().getId())
                .productoNombre(item.getProducto().getNombre())
                .varianteId(item.getVariante() != null ? item.getVariante().getId() : null)
                .varianteSku(item.getVariante() != null ? item.getVariante().getSku() : null)
                .presentacion(item.getPresentacion())
                .unidadesPorPresentacion(item.getUnidadesPorPresentacion())
                .cantidadPresentaciones(item.getCantidadPresentaciones())
                .cantidadUnidades(item.getCantidadUnidades())
                .precioUnitarioPresentacion(item.getPrecioUnitarioPresentacion())
                .precioUnitarioUnidad(item.getPrecioUnitarioUnidad())
                .subtotal(item.getSubtotal())
                .build();
    }
}
