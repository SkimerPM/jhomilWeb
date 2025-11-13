package com.jhomilmotors.jhomilwebapp.converter;
import com.jhomilmotors.jhomilwebapp.enums.ShipmentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShipmentStatusConverter implements AttributeConverter<ShipmentStatus, String> {

    @Override
    public String convertToDatabaseColumn(ShipmentStatus status) {
        if (status == null) return null;
        return status.getValue();
    }
    @Override
    public ShipmentStatus convertToEntityAttribute(String value) {
        if (value == null) return null;
        return ShipmentStatus.fromValue(value);
    }


}
