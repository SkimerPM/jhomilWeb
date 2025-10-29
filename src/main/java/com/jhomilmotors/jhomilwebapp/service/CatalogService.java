package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.repository.*;
import com.jhomilmotors.jhomilwebapp.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    @Autowired private ProductRepository productRepository;
    @Autowired private ProductVariantRepository productVariantRepository;
    @Autowired private ProductAttributeRepository productAttributeRepository;
    @Autowired private VariantAttributeRepository variantAttributeRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ImageRepository imageRepository;

    public List<ProductCatalogResponse> findAllCatalogProducts() {
        return productRepository.findAllEntities().stream()
                .map(this::mapProductToCatalogResponse)
                .collect(Collectors.toList());
    }

    private ProductCatalogResponse mapProductToCatalogResponse(Product product) {
        // VARIANTE PRINCIPAL
        ProductVariant variant = productVariantRepository
                .findFirstByProductAndActivoTrueOrderByPrecioAsc(product)
                .orElse(null);

        // IMAGEN PRINCIPAL
        Image principalImage = imageRepository.findFirstByProductAndEsPrincipalTrue(product)
                .orElse(null);

        return ProductCatalogResponse.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .descripcion(product.getDescripcion())
                .precioBase(variant != null ? variant.getPrecio() : BigDecimal.ZERO)
                .stockTotal((long) (variant != null ? variant.getStock() : 0))
                .imagenUrl(principalImage != null ? principalImage.getUrl() : "/images/placeholder.png")
                .categoriaId(product.getCategory().getId())
                .categoriaNombre(product.getCategory().getNombre())
                .marcaId(product.getBrand().getId())
                .marcaNombre(product.getBrand().getNombre())
                .build();
    }

    public ProductDetailsResponseDTO getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // ATRIBUTOS DEL PRODUCTO
        List<ProductDetailsResponseDTO.AtributoResponse> atributos =
                productAttributeRepository.findByProductId(product.getId()).stream()
                        .map(pa -> ProductDetailsResponseDTO.AtributoResponse.builder()
                                .nombre(pa.getAttribute().getNombre())
                                .codigo(pa.getAttribute().getCodigo())
                                .tipo(pa.getAttribute().getTipo())
                                .unidad(pa.getAttribute().getUnidad())
                                .valorTexto(pa.getValorText())
                                .valorNumerico(pa.getValorNum())
                                .build())
                        .collect(Collectors.toList());

        // VARIANTES
        List<ProductDetailsResponseDTO.VarianteResponse> variantes =
                productVariantRepository.findAll().stream()    // Puedes filtrar por producto y activo
                        .filter(v -> v.getProduct().getId().equals(productId) && Boolean.TRUE.equals(v.getActivo()))
                        .map(variante -> {
                            // ATRIBUTOS DE LA VARIANTE
                            List<ProductDetailsResponseDTO.AtributoResponse> atributosVar =
                                    variantAttributeRepository.findByVarianteId(variante.getId()).stream()
                                            .map(va -> ProductDetailsResponseDTO.AtributoResponse.builder()
                                                    .nombre(va.getAttribute().getNombre())
                                                    .codigo(va.getAttribute().getCodigo())
                                                    .tipo(va.getAttribute().getTipo())
                                                    .unidad(va.getAttribute().getUnidad())
                                                    .valorTexto(va.getValorText())
                                                    .valorNumerico(va.getValorNum())
                                                    .build())
                                            .collect(Collectors.toList());

                            return ProductDetailsResponseDTO.VarianteResponse.builder()
                                    .id(variante.getId())
                                    .sku(variante.getSku())
                                    .precio(variante.getPrecio())
                                    .stock(variante.getStock())
                                    .activo(variante.getActivo())
                                    .atributos(atributosVar)
                                    .build();
                        })
                        .collect(Collectors.toList());

        // IMÁGENES
        List<ProductDetailsResponseDTO.ImagenResponse> imagenes =
                imageRepository.findByProductIdOrderByOrden(productId).stream()
                        .map(img -> ProductDetailsResponseDTO.ImagenResponse.builder()
                                .url(img.getUrl())
                                .esPrincipal(img.getEsPrincipal())
                                .orden(img.getOrden())
                                .build())
                        .collect(Collectors.toList());

        return ProductDetailsResponseDTO.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .descripcion(product.getDescripcion())
                .precioBase(product.getPrecioBase())
                .marcaNombre(product.getBrand() != null ? product.getBrand().getNombre() : null)
                .categoriaNombre(product.getCategory().getNombre())
                .activo(product.getActivo())
                .atributos(atributos)
                .variantes(variantes)
                .imagenes(imagenes)
                .build();
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

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
