package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT new com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse(" +
            "p.id, " +
            "p.nombre, " +
            "p.descripcion, " +
            "p.precioBase, " +
            " (SELECT COALESCE(SUM(pv.stock), 0) FROM ProductVariant pv WHERE pv.product.id = p.id) , " + // ⬅️ Espacio extra alrededor de los paréntesis
            " (SELECT i.url FROM Image i WHERE i.product.id = p.id AND i.esPrincipal = true) , " +       // ⬅️ Espacio extra alrededor de los paréntesis
            "p.category.id, " +           // Usamos p.category.id directamente (Más limpio)
            "p.category.nombre, " +       // Usamos p.category.nombre directamente
            "m.id, " +
            "m.nombre ) " + // Nota: se cierra el paréntesis del constructor aquí.
            "FROM Product p " +
            "JOIN p.category c " +
            "LEFT JOIN p.brand m " +
            "WHERE p.activo = true " +
            "ORDER BY p.fechaCreacion DESC")
    List<ProductCatalogResponse> findCatalogData();

}