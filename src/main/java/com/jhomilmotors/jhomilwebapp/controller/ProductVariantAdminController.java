// Contenido: ProductVariantAdminController.java

package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.ProductVariantAdminDTO;
import com.jhomilmotors.jhomilwebapp.dto.ProductVariantCreateRequestDTO;
import com.jhomilmotors.jhomilwebapp.service.CatalogService; // USAMOS TU SERVICIO EXISTENTE

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
// @PreAuthorize("hasRole('ADMIN')") // Descomentar para seguridad
public class ProductVariantAdminController { // Nuevo controlador dedicado a la gestión de variantes

    // Inyectamos el CatalogService que ahora maneja la lógica de variantes
    @Autowired
    private CatalogService catalogService;

    // POST /api/v1/admin/variantes/{id}/imagen (Carga de imagen para variante)
    @PostMapping("/variantes/{id}/imagen")
    public ResponseEntity<?> subirImagenVariante(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile imagen,
            @RequestParam(value = "esPrincipal", defaultValue = "false") boolean esPrincipal
    ) {
        try {
            // El servicio ahora devuelve un Map (o un objeto de tu entidad si quieres)
            Map<String, Object> imagenData = catalogService.adjuntarImagenAVariante(id, imagen, esPrincipal);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Imagen adjuntada a la variante exitosamente",
                    "data", imagenData // Devolvemos el Map con los datos de la imagen
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
    // POST /api/v1/admin/productos/{productoId}/variantes (Creación Anidada)
    @PostMapping("/productos/{productoId}/variantes")
    public ResponseEntity<?> crearVariante(
            @PathVariable Long productoId,
                @RequestBody @Valid ProductVariantCreateRequestDTO varianteDTO
    ) {
        try {
            ProductVariantAdminDTO nuevaVariante = catalogService.crearVariante(productoId, varianteDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Variante creada exitosamente",
                    "data", nuevaVariante
            ));
        } catch (RuntimeException e) {
            // Manejo de errores (ej: Producto no encontrado o SKU duplicado)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // PUT /api/v1/admin/variantes/{id} (Actualización Individual)
    @PutMapping("/variantes/{id}")
    public ResponseEntity<?> actualizarVariante(
            @PathVariable Long id,
            @RequestBody @Valid ProductVariantCreateRequestDTO varianteDTO
    ) {
        try {
            ProductVariantAdminDTO varianteActualizada = catalogService.actualizarVariante(id, varianteDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Variante actualizada exitosamente",
                    "data", varianteActualizada
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // DELETE /api/v1/admin/variantes/{id} (Eliminación Lógica Individual)
    @DeleteMapping("/variantes/{id}")
    public ResponseEntity<?> eliminarVariante(@PathVariable Long id) {
        try {
            catalogService.eliminarVarianteLogica(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Variante eliminada (lógico) exitosamente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
    // GET /api/v1/admin/productos/{productoId}/variantes (Listar variantes de un producto)
    @GetMapping("/productos/{productoId}/variantes")
    public ResponseEntity<?> listarVariantesDeProducto(@PathVariable Long productoId) {
        try {
            // Llama a un nuevo método en CatalogService
            List<ProductVariantAdminDTO> variantes = catalogService.obtenerVariantesPorProducto(productoId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", variantes.size(),
                    "data", variantes
            ));
        } catch (RuntimeException e) {
            // Maneja el caso de que el producto no exista
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/variantes/{variantId}/imagenes/{imagenId}")
    public ResponseEntity<?> reemplazarImagenVariante(
            @PathVariable Long variantId,
            @PathVariable Long imagenId,
            @RequestParam("file") MultipartFile nuevaImagen
    ) {
        try {
            catalogService.actualizarArchivoImagenDeVariante(imagenId, nuevaImagen);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Imagen de variante reemplazada exitosamente",
                    "imagenId", imagenId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

}