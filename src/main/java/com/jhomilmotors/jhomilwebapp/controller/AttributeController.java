package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.entity.Attribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import com.jhomilmotors.jhomilwebapp.service.AttributeService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/code/{codigo}")
    public ResponseEntity<Attribute> getByCode(@PathVariable String codigo) {
        return attributeService.getByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{tipo}")
    public ResponseEntity<List<Attribute>> getByType(@PathVariable AttributeType tipo) {
        return ResponseEntity.ok(attributeService.getByTipo(tipo));
    }

    @GetMapping("/variation")
    public ResponseEntity<List<Attribute>> getAllVariationAttributes() {
        return ResponseEntity.ok(attributeService.getAllVariationAttributes());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Attribute>> searchByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(attributeService.searchByNombre(nombre));
    }
}
