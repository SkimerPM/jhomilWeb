package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.SearchResultDTO;
import com.jhomilmotors.jhomilwebapp.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/buscar")
    public ResponseEntity<List<SearchResultDTO>> buscar(@RequestParam("q") String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok(searchService.search(q.trim()));
    }
}
