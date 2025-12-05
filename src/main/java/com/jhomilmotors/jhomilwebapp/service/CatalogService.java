package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.brand.BrandRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.brand.BrandResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.*;
import com.jhomilmotors.jhomilwebapp.dto.*;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ProductAttributeRepository productAttributeRepository;
    @Autowired
    private VariantAttributeRepository variantAttributeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private AttributeRepository attributeRepository;

    // Inyectar la URL base de tu servidor de medios (la que definiste en properties)
    @Value("${app.base-media-url:}")
    private String baseMediaUrl;

//         METODO ANTIGUO LENTO
//    public List<ProductCatalogResponse> findAllCatalogProducts() {
//        return productRepository.findAllEntities().stream()
//                .map(this::mapProductToCatalogResponse)
//                .collect(Collectors.toList());
//    }

    public List<ProductCatalogResponse> findAllCatalogProducts() {
        return productRepository.findAllCatalogProductsOptimized();
    }

    public Page<AdminProductListDTO> getAllAdminProductsPaged(String search, Pageable pageable) {

        Page<Product> productPage;

        if (search != null && !search.trim().isEmpty()) {
            productPage = productRepository.searchAdminProducts(search.trim(), pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.map(AdminProductListDTO::fromEntity);
    }

    public List<ProductVariant> findVariantsByProductId(Long productId) {
        return productVariantRepository.findByProductId(productId);
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
                product.getAtributos().stream()
                        .map(pa -> ProductDetailsResponseDTO.AtributoResponse.builder()
                                .id(pa.getAttribute().getId())
                                .nombre(pa.getAttribute().getNombre())
                                .codigo(pa.getAttribute().getCodigo())
                                .tipo(String.valueOf(pa.getAttribute().getTipo()))
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
                                                    .id(va.getAttribute().getId())
                                                    .nombre(va.getAttribute().getNombre())
                                                    .codigo(va.getAttribute().getCodigo())
                                                    .tipo(String.valueOf(va.getAttribute().getTipo()))
                                                    .unidad(va.getAttribute().getUnidad())
                                                    .valorTexto(va.getValorText())
                                                    .valorNumerico(va.getValorNum())
                                                    .build())
                                            .collect(Collectors.toList());

                            // IMÁGENES DE LA VARIANTE
                            List<ProductDetailsResponseDTO.ImagenResponse> imagenesVar =
                                    imageRepository.findByVarianteIdOrderByOrdenAsc(variante.getId()).stream()
                                            .map(img -> ProductDetailsResponseDTO.ImagenResponse.builder()
                                                    .id(img.getId())
                                                    .url(img.getUrl())
                                                    .esPrincipal(img.getEsPrincipal())
                                                    .orden(img.getOrden())
                                                    .build())
                                            .collect(Collectors.toList());

                            return ProductDetailsResponseDTO.VarianteResponse.builder()
                                    .id(variante.getId())
                                    .sku(variante.getSku())
                                    .precio(variante.getPrecio())
                                    .stock(variante.getStock())
                                    .activo(variante.getActivo())
                                    .atributos(atributosVar)
                                    .imagenes(imagenesVar)
                                    .build();
                        })
                        .collect(Collectors.toList());

        // IMÁGENES
        List<ProductDetailsResponseDTO.ImagenResponse> imagenes =
                imageRepository.findByProductIdOrderByOrden(productId).stream()
                        .map(img -> ProductDetailsResponseDTO.ImagenResponse.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .esPrincipal(img.getEsPrincipal())
                                .orden(img.getOrden())
                                .build())
                        .collect(Collectors.toList());

        return ProductDetailsResponseDTO.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .descripcion(product.getDescripcion())
                .skuBase(product.getSkuBase())
                .precioBase(product.getPrecioBase())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .marcaNombre(product.getBrand() != null ? product.getBrand().getNombre() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoriaNombre(product.getCategory().getNombre())
                .activo(product.getActivo())
                .atributos(atributos)
                .variantes(variantes)
                .imagenes(imagenes)
                .build();
    }

    // Método original (para la web, solo ID y Nombre)
    public List<CategoryResponseDTO> findAllCategories() {
        return categoryRepository.findAll().stream()
                // Usa el constructor simple
                .map(c -> new CategoryResponseDTO(c.getId(), c.getNombre()))
                .collect(Collectors.toList());
    }

    // 🚀 NUEVO MÉTODO PARA EL MÓVIL (Full Categories) 🚀
    public List<CategoryResponseDTO> findAllCategoriesForMobile() {
        return categoryRepository.findAll().stream()
                .map(c -> {
                    String urlCompleta = null;
                    // Verifica si el campo de la BD (imagenUrlBase) tiene valor
                    if (c.getImagenUrlBase() != null && !c.getImagenUrlBase().isEmpty()) {
                        // Construye la URL completa que el móvil usará
                        urlCompleta = baseMediaUrl + c.getImagenUrlBase();
                    }
                    // Usa el constructor completo
                    return new CategoryResponseDTO(c.getId(), c.getNombre(), urlCompleta);
                })
                .collect(Collectors.toList());
    }

    // BRANDs

    public List<BrandResponseDTO> findAllBrands() {
        return brandRepository.findAll().stream()
                .map(m -> new BrandResponseDTO(m.getId(), m.getNombre(), m.getImagenLogoURL()))
                .collect(Collectors.toList());
    }

    public Page<BrandResponseDTO> findBrands(String nombre, Pageable pageable) {
        Page<Brand> marcas = brandRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        return marcas.map(m -> new BrandResponseDTO(m.getId(), m.getNombre(), m.getImagenLogoURL()));
    }

    public BrandResponseDTO createBrand(BrandRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio");
        }
        if (brandRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("El nombre de la marca ya existe");
        }
        Brand brand = new Brand();
        brand.setNombre(dto.getNombre().trim());
        if(dto.getImagenLogoURL() != null && !dto.getImagenLogoURL().trim().isEmpty()) {
            brand.setImagenLogoURL(dto.getImagenLogoURL().trim());
        }
        Brand saved = brandRepository.save(brand);
        return new BrandResponseDTO(saved.getId(), saved.getNombre(), saved.getImagenLogoURL());
    }

    public BrandResponseDTO updateBrand(Long id, BrandRequestDTO dto) {

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio");
        }

        Brand brand = brandRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("La marca no existe")
        );

        if (brandRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()
                && !brand.getNombre().equalsIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("El nombre de la marca ya existe");
        }

        String logoURL = dto.getImagenLogoURL();
        if (logoURL != null && !logoURL.trim().isEmpty()) {
            brand.setImagenLogoURL(logoURL.trim());
        } else {
            brand.setImagenLogoURL(null);
        }
        brand.setNombre(dto.getNombre().trim());
        Brand saved = brandRepository.save(brand);
        return new BrandResponseDTO(saved.getId(), saved.getNombre(), saved.getImagenLogoURL());
    }

    // CUIDADO: Borrado NO lógico.
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
        brandRepository.delete(brand);
    }

//    public Page<BrandResponseDTO> findAllBrand( Pageable pageable ){
//        Page<Brand> marcas = brandRepository.findAll(pageable);
//        return marcas.map(brand -> new BrandResponseDTO(brand.getId(), brand.getNombre()));
//    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<ProductDetailsResponseDTO.ImagenResponse> getImagesForProductAndVariants(Long productId) {
        // Imágenes del producto
        List<ProductDetailsResponseDTO.ImagenResponse> productImages = imageRepository.findByProductIdOrderByOrdenAsc(productId)
                .stream()
                .map(img -> ProductDetailsResponseDTO.ImagenResponse.builder()
                        .url(img.getUrl())
                        .esPrincipal(img.getEsPrincipal())
                        .orden(img.getOrden())
                        .build())
                .collect(Collectors.toList());

        // Variantes del producto
        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        List<ProductDetailsResponseDTO.ImagenResponse> variantImages = new java.util.ArrayList<>();
        for (ProductVariant v : variants) {
            List<ProductDetailsResponseDTO.ImagenResponse> imgs = imageRepository.findByVarianteIdOrderByOrdenAsc(v.getId())
                    .stream()
                    .map(img -> ProductDetailsResponseDTO.ImagenResponse.builder()
                            .url(img.getUrl())
                            .esPrincipal(img.getEsPrincipal())
                            .orden(img.getOrden())
                            .build())
                    .collect(Collectors.toList());
            variantImages.addAll(imgs);
        }
        // Une ambas listas
        List<ProductDetailsResponseDTO.ImagenResponse> allImages = new java.util.ArrayList<>();
        allImages.addAll(productImages);
        allImages.addAll(variantImages);
        return allImages;
    }

    // En CatalogService
    public List<Product> findByCategoryId(Long categoriaId) {
        return productRepository.findByCategoryId(categoriaId);
    }

    public List<Product> findByBrandId(Long brandId) {
        return productRepository.findByBrand_Id(brandId);
    }

    public List<Product> findByActivoTrue() {
        return productRepository.findByActivoTrue();
    }

    public List<Product> findByActivoFalse() {
        return productRepository.findByActivoFalse();
    }

    public Page<Product> findByNombre(String nombre, Pageable pageable) {
        return productRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    public Page<Product> findActivoTruePaged(Pageable pageable) {
        return productRepository.findByActivoTrue(pageable);
    }

    public Page<Product> findByCategoryAndActivoTrue(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActivoTrue(categoryId, pageable);
    }

    public Page<ProductCatalogResponse> findProductCatalogByCategory(Long categoryId, Pageable pageable) {
        // 1. Buscamos las Entidades (que tienen el problema del bucle)
        Page<Product> products = productRepository.findByCategoryIdAndActivoTrue(categoryId, pageable);

        // 2. Las convertimos a DTOs aquí mismo (Rompiendo el ciclo)
        return products.map(this::mapProductToCatalogResponse);
    }

    public Page<Product> findByBrandAndActivoTrue(Long brandId, Pageable pageable) {
        return productRepository.findByBrand_IdAndActivoTrue(brandId, pageable);
    }

    public Page<Product> fuzzySearchNombreDescripcion(String nombre, Pageable pageable) {
        return productRepository.searchByNombreOrDescripcion(nombre, pageable);
    }

    @Transactional(readOnly = true) // Importante para que Hibernate mantenga la sesión abierta
    public List<ProductVariantDTO> buscarEnVariantes(String q) {
        // 1. Traemos Variantes + Producto + Atributos
        List<ProductVariant> variantes = productVariantRepository.buscarConAtributos(q);

        // 2. Si encontramos algo, cargamos las imágenes en una segunda consulta
        // Hibernate "inyectará" las imágenes en la lista 'variantes' que ya tenemos en memoria.
        if (!variantes.isEmpty()) {
            productVariantRepository.cargarImagenes(variantes);
        }

        // 3. Convertimos a DTO (igual que antes)
        return variantes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    private ProductVariantDTO mapToDTO(ProductVariant entity) {
        if (entity == null) return null;

        return ProductVariantDTO.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .precio(entity.getPrecio())
                .stock(entity.getStock())
                .activo(entity.getActivo())
                .pesoKg(entity.getPesoKg())

                // Mapeo del Padre (Product) para evitar el bucle
                .productoId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productoNombre(entity.getProduct() != null ? entity.getProduct().getNombre() : "Desconocido") // Asumo que Product tiene getNombre()

                // Mapeo de la lista de Atributos
                .atributos(entity.getAtributos() != null ?
                        entity.getAtributos().stream().map(attr -> ProductVariantDTO.VarianteAtributoDTO.builder()
                                .id(attr.getId())
                                // NOTA: Ajusta estos getters según tu entidad real VariantAttribute
                                .atributoNombre(attr.getAttribute().getNombre()) // o attr.getAttribute().getNombre()
                                .valorTexto(attr.getValorText())
                                // .valorNum(attr.getValorNum()) // Si existe
                                .build()
                        ).collect(Collectors.toList()) : Collections.emptyList())

                // Mapeo de la lista de Imágenes
                .imagenes(entity.getImagenes() != null ?
                        entity.getImagenes().stream().map(img -> ProductVariantDTO.ImagenResponse.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .orden(img.getOrden()) // Si existe en tu entidad Image
                                .esPrincipal(img.getEsPrincipal()) // Si existe
                                .build()
                        ).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    @Transactional
    public Product createProductWithVariantsAndImages(ProductCreationRequestDTO request) {
        // 1. Validaciones de campos obligatorios y unicidad
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
        if (request.getSkuBase() == null || request.getSkuBase().trim().isEmpty()) {
            throw new IllegalArgumentException("El skuBase es obligatorio.");
        }
        if (productRepository.findBySkuBase(request.getSkuBase()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con ese skuBase.");
        }
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (request.getPrecioBase() == null || request.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precioBase debe ser mayor o igual a 0.");
        }
        // Validación: al menos una imagen principal
        if (request.getImagenes() == null || request.getImagenes().stream()
                .noneMatch(im -> Boolean.TRUE.equals(im.getEsPrincipal()))) {
            throw new IllegalArgumentException("El producto debe tener al menos una imagen principal (esPrincipal=true).");
        }

        // 2. Crear el producto principal
        Product product = new Product();
        product.setNombre(request.getNombre());
        product.setDescripcion(request.getDescripcion());
        product.setSkuBase(request.getSkuBase());
        product.setPrecioBase(request.getPrecioBase());
        product.setPesoKg(request.getPesoKg());
        product.setVolumenM3(request.getVolumenM3());
        product.setActivo(request.getActivo() != null ? request.getActivo() : true);
        product.setCategory(category);
        product.setBrand(brand);
        Product savedProduct = productRepository.save(product);

        // LÓGICA NUEVA: ATRIBUTOS DEL PRODUCTO
        if (request.getAtributos() != null) {
            for (ProductCreationRequestDTO.AtributoVarianteRequest attrDto : request.getAtributos()) {
                Attribute attributeDef = attributeRepository.findById(attrDto.getAtributoId())
                        .orElseThrow(() -> new IllegalArgumentException("Atributo no encontrado ID: " + attrDto.getAtributoId()));

                ProductAttribute pa = new ProductAttribute();
                pa.setProduct(savedProduct);
                pa.setAttribute(attributeDef);
                pa.setValorText(attrDto.getValor());
                // pa.setValorNum(...) si usas números

                // Opción A (Añadir a lista y guardar producto de nuevo al final):
                savedProduct.getAtributos().add(pa);
            }
            productRepository.save(savedProduct); // Actualizar con atributos
        }

        // 3. Guardar imágenes del producto principal
        for (ProductCreationRequestDTO.ImagenRequest imgDto : request.getImagenes()) {
            Image img = new Image();
            img.setProduct(savedProduct);
            img.setUrl(imgDto.getUrl());
            img.setEsPrincipal(imgDto.getEsPrincipal() != null ? imgDto.getEsPrincipal() : false);
            img.setOrden(imgDto.getOrden() != null ? imgDto.getOrden() : 0);
            imageRepository.save(img);
        }

        // 4. Guardar variantes y sus imágenes
        if (request.getVariantes() != null) {
            for (ProductCreationRequestDTO.VarianteRequest vDto : request.getVariantes()) {
                if (vDto.getSku() == null || vDto.getSku().trim().isEmpty()) {
                    throw new IllegalArgumentException("SKU de variante es obligatorio.");
                }
                if (productVariantRepository.findBySku(vDto.getSku()).isPresent()) {
                    throw new IllegalArgumentException("Ya existe una variante con el SKU: " + vDto.getSku());
                }
                if (vDto.getPrecio() == null || vDto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Precio de variante debe ser mayor o igual a 0.");
                }
                if (vDto.getStock() == null || vDto.getStock() < 0) {
                    throw new IllegalArgumentException("Stock de variante debe ser mayor o igual a 0.");
                }

                ProductVariant variant = new ProductVariant();
                variant.setProduct(savedProduct);
                variant.setSku(vDto.getSku());
                variant.setPrecio(vDto.getPrecio());
                variant.setStock(vDto.getStock());
                variant.setPesoKg(vDto.getPesoKg());
                variant.setActivo(vDto.getActivo() != null ? vDto.getActivo() : true);

                if (vDto.getAtributos() != null) {
                    for (ProductCreationRequestDTO.AtributoVarianteRequest attrDto : vDto.getAtributos()) {
                        // 1. Buscar la definición del atributo (ej: "Color")
                        Attribute attributeDef = attributeRepository.findById(attrDto.getAtributoId())
                                .orElseThrow(() -> new IllegalArgumentException("Atributo no encontrado ID: " + attrDto.getAtributoId()));

                        // 2. Crear la relación valor-variante
                        VariantAttribute va = new VariantAttribute();
                        va.setVariante(variant); // Relacionar con la variante que estamos creando
                        va.setAttribute(attributeDef);
                        va.setValorText(attrDto.getValor()); // Usar .getValor() (NO .getValorTexto())

                        // 3. Añadir a la lista de la variante
                        variant.getAtributos().add(va);
                    }
                }

                ProductVariant savedVariant = productVariantRepository.save(variant);

                if (vDto.getImagenes() != null) {
                    for (ProductCreationRequestDTO.ImagenRequest imgDto : vDto.getImagenes()) {
                        Image img = new Image();
                        img.setVariante(savedVariant);
                        img.setUrl(imgDto.getUrl());
                        img.setEsPrincipal(imgDto.getEsPrincipal() != null ? imgDto.getEsPrincipal() : false);
                        img.setOrden(imgDto.getOrden() != null ? imgDto.getOrden() : 0);
                        imageRepository.save(img);
                    }
                }
            }
        }
        return savedProduct;
    }



    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Actualiza campos uno por uno si vienen en el request
        if (request.getNombre() != null) product.setNombre(request.getNombre());
        if (request.getDescripcion() != null) product.setDescripcion(request.getDescripcion());
        if (request.getSkuBase() != null) product.setSkuBase(request.getSkuBase());
        if (request.getPrecioBase() != null) product.setPrecioBase(request.getPrecioBase());
        if (request.getPesoKg() != null) product.setPesoKg(request.getPesoKg());
        if (request.getVolumenM3() != null) product.setVolumenM3(request.getVolumenM3());
        if (request.getActivo() != null) product.setActivo(request.getActivo());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            product.setCategory(category);
        }
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
            product.setBrand(brand);
        }

        // LÓGICA NUEVA: ACTUALIZAR ATRIBUTOS DEL PRODUCTO
        if (request.getAtributos() != null) {
            product.getAtributos().clear();
            // Forzar flush para evitar unique constraint issues si los hay
            productRepository.saveAndFlush(product);

            for (ProductUpdateRequestDTO.AtributoVarianteRequest attrDto : request.getAtributos()) {
                Attribute attributeDef = attributeRepository.findById(attrDto.getAtributoId())
                        .orElseThrow(() -> new IllegalArgumentException("Atributo no encontrado ID: " + attrDto.getAtributoId()));

                ProductAttribute pa = new ProductAttribute();
                pa.setProduct(product);
                pa.setAttribute(attributeDef);
                pa.setValorText(attrDto.getValor());

                product.getAtributos().add(pa);
            }
        }

        // Imágenes
        if (request.getImagenes() != null) {

            // 1. Mapea las imágenes existentes para fácil acceso
            java.util.Map<Long, Image> imagenesActualesMap = product.getImagenes().stream()
                    .collect(java.util.stream.Collectors.toMap(Image::getId, java.util.function.Function.identity()));

            // 2. Prepara la nueva lista que reemplazará a la antigua
            java.util.List<Image> nuevaListaImagenes = new java.util.ArrayList<>();

            for (ProductUpdateRequestDTO.ImagenRequest imgDto : request.getImagenes()) {
                Image img;
                if (imgDto.getId() != null) {
                    // Es una imagen existente (Actualización)
                    img = imagenesActualesMap.remove(imgDto.getId());
                    if (img == null) {
                        // Opcional: lanzar excepción si el ID no es válido
                        throw new IllegalArgumentException("Se recibió un ID de imagen inválido: " + imgDto.getId());
                    }
                } else {
                    // Es una imagen nueva (Creación)
                    img = new Image();
                    img.setProduct(product); // ¡Importante! Asignar el padre
                }

                // 3. Actualiza/establece los datos de la imagen
                img.setUrl(imgDto.getUrl());
                img.setEsPrincipal(imgDto.getEsPrincipal() != null ? imgDto.getEsPrincipal() : false);
                img.setOrden(imgDto.getOrden() != null ? imgDto.getOrden() : 0);

                nuevaListaImagenes.add(img); // Añade a la nueva lista
            }

            // 4. Reemplaza la colección antigua por la nueva
            // Hibernate (con orphanRemoval=true) borrará automáticamente
            // cualquier imagen que quedó en 'imagenesActualesMap' (porque no vinieron en el request)
            product.getImagenes().clear();
            product.getImagenes().addAll(nuevaListaImagenes);
        }
        // Variantes
        if (request.getVariantes() != null) {
            for (ProductUpdateRequestDTO.VarianteUpdateRequest vDto : request.getVariantes()) {
                if (vDto.getEliminar() != null && vDto.getEliminar()) {
                    if (vDto.getId() != null) productVariantRepository.deleteById(vDto.getId());
                    continue;
                }
                ProductVariant variant;
                if (vDto.getId() != null) {
                    // Actualizar variante existente
                    variant = productVariantRepository.findById(vDto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
                } else {
                    // Crear nueva
                    variant = new ProductVariant();
                    variant.setProduct(product);
                }
                if (vDto.getSku() != null) variant.setSku(vDto.getSku());
                if (vDto.getPrecio() != null) variant.setPrecio(vDto.getPrecio());
                if (vDto.getStock() != null) variant.setStock(vDto.getStock());
                if (vDto.getPesoKg() != null) variant.setPesoKg(vDto.getPesoKg());
                if (vDto.getActivo() != null) variant.setActivo(vDto.getActivo());
                if (vDto.getAtributos() != null) {
                    // 1. Limpiamos los atributos anteriores de esta variante.
                    // Al hacer clear(), Hibernate detecta que faltan y los borra (si orphanRemoval=true en Entity)
                    variant.getAtributos().clear();
                    productVariantRepository.saveAndFlush(variant); //Borrado antes de update

                    for (ProductUpdateRequestDTO.AtributoVarianteRequest attrDto : vDto.getAtributos()) {
                        // 2. Buscamos la definición del atributo (ej: "Color")
                        Attribute attributeDef = attributeRepository.findById(attrDto.getAtributoId())
                                .orElseThrow(() -> new IllegalArgumentException("Atributo no encontrado ID: " + attrDto.getAtributoId()));

                        // 3. Creamos la relación valor-variante
                        VariantAttribute va = new VariantAttribute();
                        va.setVariante(variant);
                        va.setAttribute(attributeDef);
                        va.setValorText(attrDto.getValor()); // Ej: "Rojo"
                        // Si tu DTO soportara valores numéricos, iría aquí:
                        // va.setValorNum(attrDto.getValorNumerico());

                        // 4. Añadimos a la lista de la variante
                        variant.getAtributos().add(va);
                    }
                }

                ProductVariant savedVar = productVariantRepository.save(variant);
                // Imágenes de la variante
                if (vDto.getImagenes() != null) {
                    List<Image> actuales = imageRepository.findByVarianteId(savedVar.getId());
                    List<Long> idsEnNuevoRequest = vDto.getImagenes().stream()
                            .map(ProductUpdateRequestDTO.ImagenRequest::getId)
                            .filter(java.util.Objects::nonNull)
                            .collect(Collectors.toList());
                    for (Image imgActual : actuales) {
                        if (!idsEnNuevoRequest.contains(imgActual.getId())) {
                            imageRepository.delete(imgActual);
                        }
                    }
                    for (ProductUpdateRequestDTO.ImagenRequest imgDto : vDto.getImagenes()) {
                        if (imgDto.getId() != null) {
                            Image img = imageRepository.findById(imgDto.getId())
                                    .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada"));
                            img.setVariante(savedVar);
                            img.setUrl(imgDto.getUrl());
                            img.setEsPrincipal(imgDto.getEsPrincipal() != null ? imgDto.getEsPrincipal() : false);
                            img.setOrden(imgDto.getOrden() != null ? imgDto.getOrden() : 0);
                            imageRepository.save(img);
                        } else {
                            Image img = new Image();
                            img.setVariante(savedVar);
                            img.setUrl(imgDto.getUrl());
                            img.setEsPrincipal(imgDto.getEsPrincipal() != null ? imgDto.getEsPrincipal() : false);
                            img.setOrden(imgDto.getOrden() != null ? imgDto.getOrden() : 0);
                            imageRepository.save(img);
                        }
                    }
                }

            }
        }
        return productRepository.save(product);
    }


    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) throw new ResourceNotFoundException("Producto no encontrado");
        productRepository.deleteById(id);
    }



    @Transactional
    public Image addImageToProduct(Long productId, ImagenIndividualDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        Image img = new Image();
        img.setProduct(product);
        img.setUrl(dto.getUrl());
        img.setEsPrincipal(dto.getEsPrincipal() != null ? dto.getEsPrincipal() : false);
        img.setOrden(dto.getOrden() != null ? dto.getOrden() : 0);
        return imageRepository.save(img);
    }
    @Transactional
    public Image updateImage(Long imagenId, ImagenIndividualDTO dto) {
        Image img = imageRepository.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada"));
        if (dto.getUrl() != null) img.setUrl(dto.getUrl());
        if (dto.getEsPrincipal() != null) img.setEsPrincipal(dto.getEsPrincipal());
        if (dto.getOrden() != null) img.setOrden(dto.getOrden());
        return imageRepository.save(img);
    }

    @Transactional
    public void deleteImage(Long imagenId) {
        if (!imageRepository.existsById(imagenId))
            throw new IllegalArgumentException("Imagen no encontrada");
        imageRepository.deleteById(imagenId);
    }

    @Transactional
    public Image addImageToVariant(Long variantId, ImagenIndividualDTO dto) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada"));
        Image img = new Image();
        img.setVariante(variant);
        img.setUrl(dto.getUrl());
        img.setEsPrincipal(dto.getEsPrincipal() != null ? dto.getEsPrincipal() : false);
        img.setOrden(dto.getOrden() != null ? dto.getOrden() : 0);
        return imageRepository.save(img);
    }

    /**
     * Crea un producto simple y devuelve el DTO completo estándar.
     */
    @Transactional
    public ProductDetailsResponseDTO createSimpleProduct(SimpleProductRequestDTO request) {
        // 1. Validar SKU Base
        if (productRepository.findBySkuBase(request.getSku()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con ese SKU Base: " + request.getSku());
        }

        // 2. CREAR PRODUCTO PADRE
        Product product = new Product();
        product.setNombre(request.getNombre());
        product.setDescripcion(request.getDescripcion());
        product.setSkuBase(request.getSku());
        product.setPrecioBase(request.getPrecio());
        product.setPesoKg(request.getPesoKg());
        product.setVolumenM3(request.getVolumenM3());
        product.setActivo(true);

        // Relaciones
        if(request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada")));
        }
        if(request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada")));
        }

        Product savedProduct = productRepository.save(product);

        // 3. GUARDAR ATRIBUTOS DEL PRODUCTO
        if (request.getAtributos() != null && !request.getAtributos().isEmpty()) {
            List<ProductAttribute> atributosParaGuardar = new ArrayList<>();
            for (SimpleProductRequestDTO.SimpleAtributoRequest attrReq : request.getAtributos()) {
                Attribute attributeDef = attributeRepository.findById(attrReq.getAtributoId())
                        .orElseThrow(() -> new IllegalArgumentException("Atributo no encontrado ID: " + attrReq.getAtributoId()));

                ProductAttribute pa = new ProductAttribute();
                pa.setProduct(savedProduct);
                pa.setAttribute(attributeDef);
                pa.setValorText(attrReq.getValor());
                atributosParaGuardar.add(pa);
            }
            savedProduct.setAtributos(atributosParaGuardar);
            productRepository.save(savedProduct);
        }

        // 4. CREAR VARIANTE ÚNICA AUTOMÁTICA (Manejo de Stock)
        ProductVariant defaultVariant = new ProductVariant();
        defaultVariant.setProduct(savedProduct);
        defaultVariant.setSku(savedProduct.getSkuBase() + "-STD");
        defaultVariant.setPrecio(request.getPrecio());
        defaultVariant.setStock(request.getStock() != null ? request.getStock() : 0);
        defaultVariant.setPesoKg(request.getPesoKg());
        defaultVariant.setActivo(true);

        ProductVariant savedVariant = productVariantRepository.save(defaultVariant);

        // 5. GUARDAR IMÁGENES
        List<Image> savedImages = new ArrayList<>();
        if (request.getImagenes() != null && !request.getImagenes().isEmpty()) {
            List<Image> imagesToSave = new ArrayList<>();
            for (SimpleProductRequestDTO.SimpleImagenRequest imgReq : request.getImagenes()) {
                Image img = new Image();
                img.setProduct(savedProduct);
                img.setUrl(imgReq.getUrl());
                img.setEsPrincipal(imgReq.getEsPrincipal() != null ? imgReq.getEsPrincipal() : false);
                imagesToSave.add(img);
            }
            savedImages = imageRepository.saveAll(imagesToSave);
        }

        // --- CONSTRUCCIÓN DEL DTO DE RESPUESTA (ProductDetailsResponseDTO) ---
        return ProductDetailsResponseDTO.builder()
                .id(savedProduct.getId())
                .nombre(savedProduct.getNombre())
                .descripcion(savedProduct.getDescripcion())
                .skuBase(savedProduct.getSkuBase())
                .precioBase(savedProduct.getPrecioBase())
                .activo(savedProduct.getActivo())
                .categoryId(savedProduct.getCategory() != null ? savedProduct.getCategory().getId() : null)
                .categoriaNombre(savedProduct.getCategory() != null ? savedProduct.getCategory().getNombre() : null)
                .brandId(savedProduct.getBrand() != null ? savedProduct.getBrand().getId() : null)
                .marcaNombre(savedProduct.getBrand() != null ? savedProduct.getBrand().getNombre() : null)

                // Mapear Atributos
                .atributos(savedProduct.getAtributos() != null ? savedProduct.getAtributos().stream()
                        .map(attr -> ProductDetailsResponseDTO.AtributoResponse.builder()
                                .id(attr.getId())
                                .nombre(attr.getAttribute().getNombre())
                                .valorTexto(attr.getValorText())
                                .build())
                        .collect(Collectors.toList()) : Collections.emptyList())

                // Mapear Imágenes
                .imagenes(savedImages.stream()
                        .map(img -> ProductDetailsResponseDTO.ImagenResponse.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .esPrincipal(img.getEsPrincipal())
                                .orden(img.getOrden())
                                .build())
                        .collect(Collectors.toList()))

                // IMPORTANTE: Mapear la variante única a la lista de variantes
                .variantes(Collections.singletonList(
                        ProductDetailsResponseDTO.VarianteResponse.builder()
                                .id(savedVariant.getId())
                                .sku(savedVariant.getSku())
                                .precio(savedVariant.getPrecio())
                                .stock(savedVariant.getStock()) // Aquí va el stock real
                                .activo(savedVariant.getActivo())
                                .imagenes(Collections.emptyList()) // Variante simple no suele tener fotos propias si ya las tiene el padre
                                .atributos(Collections.emptyList())
                                .build()
                ))
                .promociones(Collections.emptyList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<LowStockResponseDTO> getLowStockProducts(int stockLimit) {
        List<ProductVariant> lowStockVariants = productVariantRepository.findByStockLessThanEqualAndActivoTrue(stockLimit);

        return lowStockVariants.stream().map(v -> {
            // Lógica simple para obtener la primera imagen disponible
            String imgUrl = "/images/placeholder.png";
            if (v.getImagenes() != null && !v.getImagenes().isEmpty()) {
                imgUrl = v.getImagenes().get(0).getUrl();
            } else if (v.getProduct().getImagenes() != null && !v.getProduct().getImagenes().isEmpty()) {
                imgUrl = v.getProduct().getImagenes().get(0).getUrl();
            }

            return LowStockResponseDTO.builder()
                    .varianteId(v.getId())
                    .productoNombre(v.getProduct().getNombre())
                    .sku(v.getSku())
                    .stock(v.getStock())
                    .marca(v.getProduct().getBrand() != null ? v.getProduct().getBrand().getNombre() : "Genérico")
                    .imagenUrl(imgUrl)
                    .build();
        }).collect(Collectors.toList());
    }
    // Método para el Feed Infinito del Móvil
    @Transactional(readOnly = true)
    public Page<ProductCatalogResponse> getCatalogFeed(Pageable pageable) {
        // Llama a la query JPQL optimizada que devuelve directamente el DTO paginado
        return productRepository.findCatalogFeedOptimized(pageable);
    }

}
