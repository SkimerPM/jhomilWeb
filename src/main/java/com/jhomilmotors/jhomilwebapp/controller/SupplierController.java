package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CreateSupplierDTO;
import com.jhomilmotors.jhomilwebapp.dto.SupplierDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateSupplierDTO;
import com.jhomilmotors.jhomilwebapp.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * SOLO ADMIN
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(@RequestParam String nombre) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliersByName(nombre);
        return ResponseEntity.ok(suppliers);
    }

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody CreateSupplierDTO createDTO) {
        SupplierDTO createdSupplier = supplierService.createSupplier(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSupplierDTO updateDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, updateDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
