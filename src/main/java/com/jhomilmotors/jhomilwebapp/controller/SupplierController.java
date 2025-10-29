package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CreateSupplierDTO;
import com.jhomilmotors.jhomilwebapp.dto.SupplierDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateSupplierDTO;
import com.jhomilmotors.jhomilwebapp.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard/suppliers")
@RequiredArgsConstructor

public class SupplierController {

    private final SupplierService supplierService;

    /**
     * GET /admin/dashboard/suppliers
     * Obtiene todos los proveedores
     */
    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    /**
     * GET /admin/dashboard/suppliers/{id}
     * Obtiene un proveedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    /**
     * GET /admin/dashboard/suppliers/search?nombre={nombre}
     * Busca proveedores por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(@RequestParam String nombre) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliersByName(nombre);
        return ResponseEntity.ok(suppliers);
    }

    /**
     * POST /admin/dashboard/suppliers
     * Crea un nuevo proveedor
     */
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody CreateSupplierDTO createDTO) {
        SupplierDTO createdSupplier = supplierService.createSupplier(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }

    /**
     * PUT /admin/dashboard/suppliers/{id}
     * Actualiza un proveedor existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSupplierDTO updateDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, updateDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    /**
     * DELETE /admin/dashboard/suppliers/{id}
     * Elimina un proveedor
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
