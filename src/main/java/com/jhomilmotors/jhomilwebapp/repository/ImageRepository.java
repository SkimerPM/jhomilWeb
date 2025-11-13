package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    /**
     * Busca la primera imagen marcada como principal para un producto.
     */
    Optional<Image> findFirstByProductAndEsPrincipalTrue(Product product);

    /**
     * Busca todas las imágenes de un producto, ordenadas por campo "orden".
     */
    List<Image> findByProductIdOrderByOrden(Long productId);


    List<Image> findByProductId(Long productoId);
    List<Image> findByVarianteId(Long varianteId);

    Optional<Image> findByProductIdAndEsPrincipalTrue(Long productoId);
    Optional<Image> findByVarianteIdAndEsPrincipalTrue(Long varianteId);

    List<Image> findByProductIdOrderByOrdenAsc(Long productoId);
    List<Image> findByVarianteIdOrderByOrdenAsc(Long varianteId);

    @Query("SELECT i FROM Image i WHERE i.product.id = :productoId OR i.variante.id = :varianteId ORDER BY i.orden")
    List<Image> findByProductoOrVariante(@Param("productoId") Long productoId, @Param("varianteId") Long varianteId);

}