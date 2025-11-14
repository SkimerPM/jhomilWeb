package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CloudinaryImageController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, String> resp = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(resp);
        } catch (IOException ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/delete-image/{publicId}")
    public ResponseEntity<?> deleteImage(@PathVariable String publicId) {
        boolean ok = cloudinaryService.deleteImage(publicId);
        return ok
                ? ResponseEntity.ok(Map.of("result", "ok"))
                : ResponseEntity.status(500).body(Map.of("result", "error"));
    }


}
