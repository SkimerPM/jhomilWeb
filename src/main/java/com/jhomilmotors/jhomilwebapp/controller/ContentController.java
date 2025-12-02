package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.entity.AppContent;
import com.jhomilmotors.jhomilwebapp.repository.AppContentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final AppContentRepository repository;

    public ContentController(AppContentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> getContent(@PathVariable String codigo) {
        AppContent content = repository.findByCodigo(codigo).orElse(null);

        if (content == null) {
            return ResponseEntity.notFound().build();
        }

        // Convertir HTML a Base64
        String originalHtml = content.getHtmlContent();
        String encodedHtml = Base64.getEncoder().encodeToString(originalHtml.getBytes());

        return ResponseEntity.ok(Map.of(
                "codigo", content.getCodigo(),
                "contentBase64", encodedHtml
        ));
    }
}