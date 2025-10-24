package com.jhomilmotors.jhomilwebapp.dto;

import lombok.AllArgsConstructor; // ⬅️ ¡Nueva importación clave!
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor // ⬅️ ¡Necesario para la consulta HQL!
public class ProductCatalogResponse {
    private Long id; // 1. p.id
    private String nombre; // 2. p.nombre
    private String descripcion; // 3. p.descripcion

    // El nombre del campo debe coincidir con los tipos y orden de la consulta HQL
    private BigDecimal precioBase; // 4. p.precioBase (Asumiendo que 'p.precioBase' es BigDecimal en la entidad)
    private Long stockTotal; // 5. COALESCE(SUM(pv.stock), 0) (Debe ser Long si pv.stock es Long/Integer)
    private String imagenUrl; // 6. i.url

    private Long categoriaId; // 7. p.category.id
    private String categoriaNombre; // 8. p.category.nombre
    private Long marcaId; // 9. m.id
    private String marcaNombre; // 10. m.nombre
}