package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.entity.ExportJob;

import com.jhomilmotors.jhomilwebapp.enums.ExportJobType;
import com.jhomilmotors.jhomilwebapp.service.ExportJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/export-jobs")
@RequiredArgsConstructor
public class ExportJobController {
    private final ExportJobService exportJobService;

    @GetMapping
    public ResponseEntity<List<ExportJob>> getAll() {
        return ResponseEntity.ok(exportJobService.getAll());
    }

    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<List<ExportJob>> getByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(exportJobService.getByUsuarioId(usuarioId));
    }

    @GetMapping("/user/{usuarioId}/paged")
    public ResponseEntity<Page<ExportJob>> getByUsuarioPaged(@PathVariable Long usuarioId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(exportJobService.getByUsuarioIdPaged(usuarioId, pageable));
    }

    @GetMapping("/type/{tipo}")
    public ResponseEntity<List<ExportJob>> getByTipo(@PathVariable ExportJobType tipo) {
        return ResponseEntity.ok(exportJobService.getByTipo(tipo));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ExportJob>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(exportJobService.getByStatus(status));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ExportJob>> getByFechaCreacionAfter(@RequestParam String fecha) {
        // Espera fecha en formato ISO yyyy-MM-ddTHH:mm:ss
        return ResponseEntity.ok(exportJobService.getByFechaCreacionAfter(LocalDateTime.parse(fecha)));
    }
}
