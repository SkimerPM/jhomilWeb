package com.jhomilmotors.jhomilwebapp.converter;
import com.jhomilmotors.jhomilwebapp.enums.InventoryMovementType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InventoryMovementTypeConverter  implements AttributeConverter<InventoryMovementType, String> {

    @Override
    public String convertToDatabaseColumn(InventoryMovementType type) {
        if (type == null) return null;
        return type.getValue();
    }
    @Override
    public InventoryMovementType convertToEntityAttribute(String value) {
        if (value == null) return null;
        return InventoryMovementType.fromValue(value);
    }

}
