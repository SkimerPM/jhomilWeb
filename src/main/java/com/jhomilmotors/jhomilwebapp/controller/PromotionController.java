package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.PromotionDTO;
import com.jhomilmotors.jhomilwebapp.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService service;

    // CRUD
    @GetMapping
    public List<PromotionDTO> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public PromotionDTO getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PromotionDTO create(@RequestBody PromotionDTO dto) { return service.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PromotionDTO update(@PathVariable Long id, @RequestBody PromotionDTO dto) { return service.update(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }

    // Queries avanzadas
    @GetMapping("/active")
    public List<PromotionDTO> getActive() { return service.getActive(); }

    @GetMapping("/tipo-descuento/{tipo}")
    public List<PromotionDTO> getByTipoDescuento(@PathVariable String tipo) { return service.getByTipoDescuento(tipo); }

    @GetMapping("/active-tipo-descuento/{tipo}")
    public List<PromotionDTO> getActiveByTipoDescuento(@PathVariable String tipo) { return service.getActiveByTipoDescuento(tipo); }

    @GetMapping("/codigo/{code}")
    public PromotionDTO getByCodigo(@PathVariable String code) { return service.getByCodigo(code); }

    @GetMapping("/vigentes")
    public List<PromotionDTO> getVigentes() { return service.getVigentes(); }

    @GetMapping("/activas-vigentes")
    public List<PromotionDTO> getActivasVigentes() { return service.getActivasVigentes(); }
}
