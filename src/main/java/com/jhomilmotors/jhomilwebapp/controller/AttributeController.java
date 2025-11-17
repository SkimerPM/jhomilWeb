package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.attribute.AttributeRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.attribute.AttributeResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import com.jhomilmotors.jhomilwebapp.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/attributes")
@RequiredArgsConstructor
public class AttributeController {
    private final AttributeService attributeService;

    @GetMapping
    public ResponseEntity<List<Attribute>> getAllInOrder() {
        return ResponseEntity.ok(attributeService.getAllOrdered());
    }

    @GetMapping
    public Page<AttributeResponseDTO> listAll(@PageableDefault(size = 20) Pageable pageable) {
        return attributeService.listAll(pageable);
    }


    @GetMapping("/code/{codigo}")
    public ResponseEntity<Attribute> getByCode(@PathVariable String codigo) {
        return attributeService.getByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<AttributeResponseDTO> searchByNombre(
            @RequestParam String nombre, Pageable pageable
    ){
        return attributeService.searchByNombre(nombre, pageable);
    }
    @PostMapping
    public AttributeResponseDTO create(@RequestBody AttributeRequestDTO dto) {
        return attributeService.create(dto);
    }
    @PutMapping("/{id}")
    public AttributeResponseDTO update(@PathVariable Long id, @RequestBody AttributeRequestDTO dto) {
        return attributeService.update(id, dto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        attributeService.delete(id);
    }

}
