package com.jhomilmotors.jhomilwebapp.converter;

import com.jhomilmotors.jhomilwebapp.enums.DiscountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DiscountTypeConverter implements AttributeConverter<DiscountType, String> {
    @Override
    public String convertToDatabaseColumn(DiscountType discountType) {
        if (discountType == null) return null;
        return discountType.getValue(); // guarda "porcentaje", "monto_fijo", etc.
    }

    // Convierte de la base de datos a enum al leer
    @Override
    public DiscountType convertToEntityAttribute(String value) {
        if (value == null) return null;
        for (DiscountType type : DiscountType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de descuento desconocido: " + value);
    }
}
