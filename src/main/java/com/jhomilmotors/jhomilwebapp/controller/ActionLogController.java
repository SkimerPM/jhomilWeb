package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.entity.ActionLog;
import com.jhomilmotors.jhomilwebapp.service.ActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/action-logs")
@RequiredArgsConstructor
public class ActionLogController {
    private final ActionLogService actionLogService;

    // 1. Todos los logs
    @GetMapping
    public ResponseEntity<List<ActionLog>> getAllLogs() {
        return ResponseEntity.ok(actionLogService.getAllLogs());
    }

    // 2. Logs por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActionLog>> getLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(actionLogService.getLogsByUserId(userId));
    }

    // 3. Logs por usuario paginado
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<ActionLog>> getLogsByUserPaged(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(actionLogService.getLogsByUserIdPaged(userId, pageable));
    }

    // 4. Logs por rango de fechas
    @GetMapping("/between")
    public ResponseEntity<List<ActionLog>> getLogsBetween(
            @RequestParam("inicio") String inicio,
            @RequestParam("fin") String fin) {
        LocalDateTime start = LocalDateTime.parse(inicio);
        LocalDateTime end = LocalDateTime.parse(fin);
        return ResponseEntity.ok(actionLogService.getLogsBetweenDates(start, end));
    }

    // 5. Buscar por acción
    @GetMapping("/search")
    public ResponseEntity<List<ActionLog>> searchByAccion(@RequestParam String accion) {
        return ResponseEntity.ok(actionLogService.searchLogsByAccion(accion));
    }
}
