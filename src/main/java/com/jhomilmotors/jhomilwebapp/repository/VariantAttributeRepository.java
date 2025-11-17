package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.VariantAttribute;
import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {
    /**
     * Caso de uso: Listar todos los atributos asociados a una variante específica.
     * Ejemplo: Al mostrar el detalle o ficha de una variante en el admin/cliente.
     */
    Page<VariantAttribute> findByVarianteId(Long varianteId, Pageable pageable);

    // 👇 AGREGA ESTE: Para el detalle del producto (Trae todos los de esa variante)
    List<VariantAttribute> findByVarianteId(Long varianteId);

    /**
     * Caso de uso: Listar los atributos de una variante filtrando por tipo (ejemplo: solo "compatibilidad" o "spec").
     * Útil para mostrar secciones diferentes en la UI o para paneles admin con filtros avanzados.
     */
    Page<VariantAttribute> findByVarianteIdAndAttribute_Tipo(Long varianteId, AttributeType tipo, Pageable pageable);

    /**
     * Caso de uso: Buscar atributos asociados a una variante por nombre parcial.
     * Útil para el panel admin al buscar atributos tipo "color", "descripción", etc. de manera rápida.
     */
    Page<VariantAttribute> findByVarianteIdAndAttribute_NombreContainingIgnoreCase(Long varianteId, String nombre, Pageable pageable);

    /**
     * Caso de uso: Listar todas las instancias de VariantAttribute que usan un mismo atributo, es decir,
     * todas las variantes que tienen ese atributo asignado (ejemplo: "Color" presente en muchas variantes).
     */
    Page<VariantAttribute> findByAttributeId(Long attributeId, Pageable pageable);

    /**
     * Caso de uso: Buscar una relación exacta variante-atributo (para validación de unicidad antes de crear o editar)
     * Útil para evitar duplicados por el constraint unique o para editar el valor existente de un atributo específico.
     */
    Optional<VariantAttribute> findByVarianteIdAndAttributeId(Long varianteId, Long attributeId);

}