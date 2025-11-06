package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.*;

import com.jhomilmotors.jhomilwebapp.repository.*;
import com.jhomilmotors.jhomilwebapp.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private ProductDetailsResponseDTO.ImagenResponse mapToImagenResponse(Image image) {
        // Reutilizamos el builder que ya corregimos en el DTO
        return ProductDetailsResponseDTO.ImagenResponse.builder()
                .url(image.getUrl())
                .esPrincipal(image.getEsPrincipal())
                .orden(image.getOrden())
                .build();
    }

    // Ruta configurable desde application.properties
    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    // ============================================
    // CREAR PRODUCTO CON IMAGEN Y VARIANTE INICIAL
    // ============================================
    @Transactional
    public ProductCatalogResponse crearProductoConImagen(ProductCreateRequestDTO dto, MultipartFile imagen) {

        // 1. Validar categoría (requerida en Django)
        Category categoria = categoryRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId()));

        // 2. Validar marca (opcional en Django)
        Brand marca = null;
        if (dto.getMarcaId() != null) {
            marca = brandRepository.findById(dto.getMarcaId())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + dto.getMarcaId()));
        }

        // 3. Validar SKU único del producto
        if (dto.getSkuBase() != null && productRepository.existsBySkuBase(dto.getSkuBase())) {
            throw new RuntimeException("El SKU base ya existe: " + dto.getSkuBase());
        }

        // 4. Crear el PRODUCTO base (equivalente a Producto en Django)
        Product producto = new Product();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setSkuBase(dto.getSkuBase());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setPesoKg(dto.getPesoKg());
        producto.setVolumenM3(dto.getVolumenM3());
        producto.setCategory(categoria);
        producto.setBrand(marca);
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());

        Product productoGuardado = productRepository.save(producto);

        // Validar SKU único de la variante
        if (productVariantRepository.existsBySku(dto.getSkuVariante())) {
            throw new RuntimeException("El SKU de la variante ya existe: " + dto.getSkuVariante());
        }

        // 5. Crear la VARIANTE inicial (ProductoVariante en Django)
        // Esto es importante porque en Django siempre tienes variantes
        ProductVariant variante = new ProductVariant();
        variante.setProduct(productoGuardado);
        variante.setSku(dto.getSkuVariante());
        variante.setPrecio(dto.getPrecioVariante());
        variante.setStock(dto.getStockInicial());
        variante.setPesoKg(dto.getPesoKg());
        variante.setActivo(true);
        variante.setFechaCreacion(LocalDateTime.now());

        ProductVariant varianteGuardada = productVariantRepository.save(variante);


        // 6. Guardar imagen si se proporciona (equivalente a Imagen en Django)
        if (imagen != null && !imagen.isEmpty()) {
            String urlImagen = guardarImagenEnServidor(imagen, productoGuardado.getId());

            Image imagenEntidad = new Image();
            imagenEntidad.setProduct(productoGuardado);
            imagenEntidad.setVariant(null); // Imagen del producto, no de variante específica
            imagenEntidad.setUrl(urlImagen);
            imagenEntidad.setEsPrincipal(dto.getImagenPrincipal() != null ? dto.getImagenPrincipal() : true);
            imagenEntidad.setOrden(0);

            imageRepository.save(imagenEntidad);
        }

        // 7. Retornar respuesta con datos del producto creado
        return mapProductToCatalogResponse(productoGuardado);
    }

    // ============================================
    // GUARDAR IMAGEN FÍSICAMENTE EN EL SERVIDOR
    // ============================================
    private String guardarImagenEnServidor(MultipartFile imagen, Long productId) {
        try {
            // Validar que sea una imagen
            String contentType = imagen.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("El archivo debe ser una imagen válida");
            }

            // Validar tamaño (máximo 5MB)
            if (imagen.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("La imagen no debe superar los 5MB");
            }

            // Generar nombre único para evitar colisiones
            String originalFilename = imagen.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";

            String nombreArchivo = "prod_" + productId + "_" + UUID.randomUUID() + extension;

            // Crear directorio si no existe
            Path directorioUpload = Paths.get(uploadDir);
            if (!Files.exists(directorioUpload)) {
                Files.createDirectories(directorioUpload);
            }

            // Guardar archivo en el servidor
            Path rutaArchivo = directorioUpload.resolve(nombreArchivo);
            Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Retornar URL pública para el frontend
            return "/images/" + nombreArchivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen en el servidor: " + e.getMessage(), e);
        }
    }

    // ============================================
    // MÉTODOS EXISTENTES (LISTADO Y DETALLE)
    // ============================================

    public List<ProductCatalogResponse> findAllCatalogProducts() {
        return productRepository.findAllEntities().stream()
                .map(this::mapProductToCatalogResponse)
                .collect(Collectors.toList());
    }

    private ProductCatalogResponse mapProductToCatalogResponse(Product product) {
        // VARIANTE PRINCIPAL (con menor precio y activa)
        ProductVariant variant = productVariantRepository
                .findFirstByProductAndActivoTrueOrderByPrecioAsc(product)
                .orElse(null);

        // IMAGEN PRINCIPAL
        Image principalImage = imageRepository.findFirstByProductAndEsPrincipalTrue(product)
                .orElse(null);

        // ⬇️ ¡CORRECCIÓN CLAVE! USAR EL CONSTRUCTOR MANUAL DE 11 ARGUMENTOS ⬇️
        return new ProductCatalogResponse(
                product.getId(),                                                // 1. id
                product.getNombre(),                                            // 2. nombre
                product.getDescripcion(),                                       // 3. descripcion
                // Precio Base: usa el precio de la variante si existe, sino el del producto
                variant != null ? variant.getPrecio() : product.getPrecioBase(), // 4. precioBase
                // Stock Total: usa el stock de la variante si existe, sino 0
                (long) (variant != null ? variant.getStock() : 0),              // 5. stockTotal
                // Imagen URL: usa la imagen principal si existe
                principalImage != null ? principalImage.getUrl() : "/images/placeholder.png", // 6. imagenUrl
                product.getCategory().getId(),                                  // 7. categoriaId
                product.getCategory().getNombre(),                              // 8. categoriaNombre
                product.getBrand() != null ? product.getBrand().getId() : null, // 9. marcaId
                product.getBrand() != null ? product.getBrand().getNombre() : null, // 10. marcaNombre
                // SKU: usa el SKU de la variante si existe, sino el skuBase
                variant != null ? variant.getSku() : product.getSkuBase()       // 11. sku
        );
        // ⬆️ ESTA LÍNEA REEMPLAZA A TODO EL BLOQUE .builder()...build() ⬆️
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
        List<ProductVariantAdminDTO> variantes =
                productVariantRepository.findAll().stream()
                        .filter(v -> v.getProduct().getId().equals(productId) && Boolean.TRUE.equals(v.getActivo()))
                        .map(variante -> {
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

                            return ProductVariantAdminDTO.fromEntity(variante);
                        })
                        .collect(Collectors.toList());

        // IMÁGENES
        List<ProductDetailsResponseDTO.ImagenResponse> imagenes =

                imageRepository.findByProductIdAndVariantIsNullOrderByOrden(productId).stream()
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

    @Transactional
    public ProductCatalogResponse actualizarProducto(Long id, ProductCreateRequestDTO dto) {
        Product producto = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setSkuBase(dto.getSkuBase());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setPesoKg(dto.getPesoKg());
        producto.setVolumenM3(dto.getVolumenM3());
        producto.setActivo(true);
        // Marca/categoría si cambian
        if(dto.getCategoriaId() != null)
            producto.setCategory(categoryRepository.findById(dto.getCategoriaId()).orElseThrow());
        if(dto.getMarcaId() != null)
            producto.setBrand(brandRepository.findById(dto.getMarcaId()).orElse(null)); // null si no hay marca

        productRepository.save(producto); // Guarda el producto modificado
        return mapProductToCatalogResponse(producto);
    }

    @Transactional
    public ProductCatalogResponse patchProducto(Long id, Map<String, Object> fields) {
        Product producto = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        // Actualiza solo los campos presentes
        fields.forEach((k, v) -> {
            switch(k) {
                case "nombre": producto.setNombre((String) v); break;
                case "descripcion": producto.setDescripcion((String) v); break;
                case "precioBase":
                    if (v instanceof Number) {
                        producto.setPrecioBase(BigDecimal.valueOf(((Number) v).doubleValue()));
                    } else {
                        producto.setPrecioBase(new BigDecimal(v.toString()));
                    }
                    break;
                // Agrega más casos según todos tus campos editables
            }
        });
        productRepository.save(producto);
        return mapProductToCatalogResponse(producto);
    }

    @Transactional
    public void deleteProductoLogico(Long id) {
        Product producto = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(false);
        productRepository.save(producto);
    }
    @Transactional
    public void actualizarArchivoImagen(Long imagenId, MultipartFile nuevaImagen) {
        Image img = imageRepository.findById(imagenId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        // Guarda nuevo archivo físico usando lógica similar a guardarImagenEnServidor
        String urlNueva = guardarImagenEnServidor(nuevaImagen, img.getProduct().getId());

        // Elimina el antiguo archivo físico si lo deseas (opcional, para no acumular basura)
        // Puedes hacer Files.deleteIfExists(Paths.get("uploads/products/...", img.getUrl()));

        img.setUrl(urlNueva);
        imageRepository.save(img);
    }


    // Nuevo método para la vista de administración
    @Transactional(readOnly = true)
    public Page<ProductAdminResponseDTO> listarTodos(Pageable pageable) {

        // 1. Obtener la página de entidades Product con sus FETCH JOINs
        Page<Product> productPage = productRepository.findAllWithVariantsForAdmin(pageable);

        // 2. Mapear la página de entidades a la página de DTOs
        return productPage.map(ProductAdminResponseDTO::fromEntity);
    }

    // Método para el catálogo público (mantener si es necesario)
    @Transactional(readOnly = true)
    public Page<ProductCatalogResponse> getPaginatedCatalog(Pageable pageable) {
        return productRepository.listarCatalogo(pageable);
    }


    // ============================================
// GESTIÓN DE VARIANTES (NUEVOS MÉTODOS)
// ============================================

    // Método auxiliar de mapeo que utiliza tu DTO existente
    private ProductVariantAdminDTO mapVariantToAdminDTO(ProductVariant variante) {

        // 1. Mapeo inicial
        ProductVariantAdminDTO dto = ProductVariantAdminDTO.fromEntity(variante);

        // 2. Carga la lista de imágenes desde el repositorio (DB)
        List<Image> imagenes = imageRepository.findByVariantIdOrderByOrden(variante.getId()); // Usar 'VariantId'

        // 3. Mapea y asigna las imágenes cargadas al DTO
        List<ProductDetailsResponseDTO.ImagenResponse> imagenDtos = imagenes.stream()
                .map(this::mapToImagenResponse) // Tu método de mapeo de Image
                .collect(Collectors.toList());

        dto.setImagenes(imagenDtos);
        // Usamos el método estático que definiste en ProductVariantAdminDTO.java
        return dto;
    }

    /**
     * Crea una nueva variante para un producto existente.
     * POST /api/v1/admin/productos/{productId}/variantes
     */
    @Transactional
    public ProductVariantAdminDTO crearVariante(Long productId, ProductVariantCreateRequestDTO dto) {
        // 1. Verificar si el producto padre existe
        Product productoPadre = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // 2. Validar SKU único de la variante
        if (productVariantRepository.existsBySku(dto.getSku())) {
            throw new RuntimeException("El SKU de la variante ya existe: " + dto.getSku());
        }

        // 3. Crear la entidad Variante
        ProductVariant nuevaVariante = new ProductVariant();
        nuevaVariante.setProduct(productoPadre);
        nuevaVariante.setSku(dto.getSku());
        nuevaVariante.setPrecio(dto.getPrecio());
        nuevaVariante.setStock(dto.getStock());
        // Asumo que tu DTO de entrada no incluye peso, si lo incluye, agrégalo:
        // nuevaVariante.setPesoKg(dto.getPesoKg());
        nuevaVariante.setActivo(true);
        nuevaVariante.setFechaCreacion(LocalDateTime.now());

        // 4. Guardar y devolver el DTO
        ProductVariant varianteGuardada = productVariantRepository.save(nuevaVariante);
        return mapVariantToAdminDTO(varianteGuardada);
    }


    /**
     * Actualiza una variante específica.
     * PUT /api/v1/admin/variantes/{variantId}
     */
    @Transactional
    public ProductVariantAdminDTO actualizarVariante(Long variantId, ProductVariantCreateRequestDTO dto) {
        // 1. Encontrar la variante
        ProductVariant varianteExistente = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada con ID: " + variantId));

        // 2. Verificar que el nuevo SKU no exista en otra variante
        if (!varianteExistente.getSku().equals(dto.getSku()) && productVariantRepository.existsBySku(dto.getSku())) {
            throw new RuntimeException("El nuevo SKU de la variante ya existe en otro registro: " + dto.getSku());
        }

        // 3. Actualizar campos
        varianteExistente.setSku(dto.getSku());
        varianteExistente.setPrecio(dto.getPrecio());
        varianteExistente.setStock(dto.getStock());
        // Si manejas peso en el DTO de entrada, actualízalo aquí.

        // 4. Guardar y devolver
        ProductVariant varianteActualizada = productVariantRepository.save(varianteExistente);
        return mapVariantToAdminDTO(varianteActualizada);
    }


    /**
     * Elimina lógicamente una variante.
     * DELETE /api/v1/admin/variantes/{variantId}
     */
    @Transactional
    public void eliminarVarianteLogica(Long variantId) {
        ProductVariant varianteExistente = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada con ID: " + variantId));

        // Setear activo a false (eliminación lógica)
        varianteExistente.setActivo(false);
        productVariantRepository.save(varianteExistente);
    }

    /**
     * Obtiene todas las variantes activas de un producto específico.
     * GET /api/v1/admin/productos/{productoId}/variantes
     */
    @Transactional(readOnly = true)
    public List<ProductVariantAdminDTO> obtenerVariantesPorProducto(Long productId) {
        // 1. Opcional: Verificar si el producto padre existe (para lanzar 404 claro)
        // Puedes comentar esto si prefieres que el Repositorio devuelva lista vacía
        // si el productoId no existe. Pero es mejor lanzar una excepción clara:
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Producto no encontrado con ID: " + productId);
        }

        // 2. Usar el Repository para buscar las variantes activas por ID de producto
        List<ProductVariant> variantes = productVariantRepository
                .findByProductIdAndActivoTrue(productId);

        // 3. Mapear las entidades a DTOs y devolver
        return variantes.stream()
                .map(this::mapVariantToAdminDTO) // Reutilizamos tu método auxiliar
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> adjuntarImagenAVariante(Long variantId, MultipartFile imagen, boolean esPrincipal) {
        // 1. Verificar si la variante existe
        ProductVariant variante = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada con ID: " + variantId));

        // 2. Guardar imagen físicamente
        // Reutilizamos el método existente, usando el ID del producto padre para la ruta
        String urlImagen = guardarImagenEnServidor(imagen, variante.getProduct().getId());

        // 3. Crear y guardar la entidad Image
        Image imagenEntidad = new Image();
        imagenEntidad.setProduct(variante.getProduct()); // Necesario por la FK de tu modelo Django
        imagenEntidad.setVariant(variante);             // Lo clave: se asocia a la variante
        imagenEntidad.setUrl(urlImagen);
        imagenEntidad.setEsPrincipal(esPrincipal);
        imagenEntidad.setOrden(0);

        Image imagenGuardada = imageRepository.save(imagenEntidad);

        // 4. Retornar un Map con los datos clave (simulando un DTO rápido)
        return Map.of(
                "id", imagenGuardada.getId(),
                "url", imagenGuardada.getUrl(),
                "esPrincipal", imagenGuardada.getEsPrincipal(),
                "orden", imagenGuardada.getOrden(),
                "varianteId", variante.getId()
        );
    }

    @Transactional
    public void actualizarArchivoImagenDeVariante(Long imagenId, MultipartFile nuevaImagen) {
        Image imagenExistente = imageRepository.findById(imagenId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con ID: " + imagenId));

        // 1. Opcional: Eliminar el archivo antiguo del servidor (para ahorrar espacio)
        // El método guardarImagenEnServidor automáticamente sobrescribe si usas el mismo nombre.
        // Si NO quieres guardar el archivo antiguo, debes hacer una lógica de eliminación aquí.

        // 2. Guardar la nueva imagen (la lógica de guardar Imagen es la misma)
        // Usamos el ID del producto padre para la ruta
        String urlImagen = guardarImagenEnServidor(nuevaImagen, imagenExistente.getProduct().getId());

        // 3. Actualizar la URL de la imagen en la base de datos
        imagenExistente.setUrl(urlImagen);
        // El resto de campos (esPrincipal, orden, variant) permanecen igual
        imageRepository.save(imagenExistente);
    }
}
