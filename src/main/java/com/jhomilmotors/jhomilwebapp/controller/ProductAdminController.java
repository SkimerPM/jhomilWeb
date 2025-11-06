package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import com.jhomilmotors.jhomilwebapp.dto.ProductCreateRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.ProductDetailsResponseDTO;
import com.jhomilmotors.jhomilwebapp.service.CatalogService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jhomilmotors.jhomilwebapp.dto.ProductAdminResponseDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController // ← IMPORTANTE: Esta anotación faltaba
@RequestMapping("/api/v1/admin/productos")
//@PreAuthorize("hasRole('ADMIN')") // Solo administradores
public class ProductAdminController {

    @Autowired // ← Inyectar el servicio, no llamar estáticamente
    private CatalogService catalogService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> crearProducto(
            @RequestPart("producto") @Valid ProductCreateRequestDTO productoDTO,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            ProductCatalogResponse productoCreado = catalogService.crearProductoConImagen(productoDTO, imagen);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado exitosamente");
            response.put("data", productoCreado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "BAD_REQUEST");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProducto(@PathVariable Long id) {
        try {
            // Llama a tu service real
            ProductDetailsResponseDTO producto = catalogService.getProductDetails(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //paginacion listar todos los productos:
    @GetMapping()
    public Page<ProductAdminResponseDTO> listarProductos(Pageable pageable) {
        return catalogService.listarTodos(pageable);
    }
    // PUT: actualizar producto (JSON, sin imagen)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductCreateRequestDTO productoDTO
    ) {
        try {
            ProductCatalogResponse productoActualizado = catalogService.actualizarProducto(id, productoDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Producto actualizado exitosamente",
                    "data", productoActualizado
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // PATCH: solo algunos campos
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProducto(
            @PathVariable Long id,
            @RequestBody Map<String, Object> fields
    ) {
        try {
            ProductCatalogResponse resp = catalogService.patchProducto(id, fields);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Producto modificado parcialmente",
                    "data", resp
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // DELETE lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Long id) {
        catalogService.deleteProductoLogico(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Producto eliminado (lógico)"));
    }

    @PutMapping("/{productoId}/imagenes/{imagenId}")
    public ResponseEntity<?> actualizarArchivoImagen(
            @PathVariable Long productoId,
            @PathVariable Long imagenId,
            @RequestPart("imagen") MultipartFile nuevaImagen
    ) {
        catalogService.actualizarArchivoImagen(imagenId, nuevaImagen);
        return ResponseEntity.ok(Map.of("success", true, "message", "Imagen actualizada"));
    }

}
