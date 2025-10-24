package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.repository.*;
import com.jhomilmotors.jhomilwebapp.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    // Inyección de dependencias de todos los repositorios necesarios
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductVariantRepository variantRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ImageRepository imageRepository;

    // --- Métodos de Mapeo y Lógica ---

    private ProductCatalogResponse mapProductToCatalogResponse(Product product) {
        // Lógica 1: Obtener la VARIANTE PRINCIPAL para el precio y stock
        ProductVariant variant = variantRepository.findFirstByProductAndActivoTrueOrderByPrecioAsc(product)
                .orElse(null);

        // Lógica 2: Obtener la IMAGEN PRINCIPAL
        Image principalImage = imageRepository.findFirstByProductAndEsPrincipalTrue(product)
                .orElse(null);

        // Construir el DTO
        return ProductCatalogResponse.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .descripcion(product.getDescripcion())

                // Datos de la Variante
                .precioBase(variant != null ? variant.getPrecio() : BigDecimal.ZERO)
                .stockTotal((long) (variant != null ? variant.getStock() : 0))

                // Imagen
                .imagenUrl(principalImage != null ? principalImage.getUrl() : "/images/placeholder.png")

                // Relaciones (de la entidad Product)
                .categoriaId(product.getCategory().getId())
                .categoriaNombre(product.getCategory().getNombre())
                .marcaId(product.getBrand().getId())
                .marcaNombre(product.getBrand().getNombre())
                .build();
    }

    // --- Métodos de Exposición al Controlador ---

    public List<ProductCatalogResponse> findAllCatalogProducts() {
        // En un ambiente real, se debe optimizar para evitar el problema de N+1 consultas.
        // Aquí cargamos todos los productos activos.
        return productRepository.findAll().stream()
                .filter(Product::getActivo)
                .map(this::mapProductToCatalogResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponseDTO> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryResponseDTO(c.getId(), c.getNombre()))
                .collect(Collectors.toList());
    }

    public List<BrandResponseDTO> findAllBrands() {
        return brandRepository.findAll().stream()
                .map(m -> new BrandResponseDTO(m.getId(), m.getNombre()))
                .collect(Collectors.toList());
    }
}