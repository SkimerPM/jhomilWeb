package com.jhomilmotors.jhomilwebapp.converter;

import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter(autoApply = true)
public class AttributeTypeConverter implements AttributeConverter<AttributeType, String> {
    @Override
    public String convertToDatabaseColumn(AttributeType attribute) {
        if (attribute == null) return null;
        return attribute.getValue();
    }
    @Override
    public AttributeType convertToEntityAttribute(String value) {
        if (value == null) return null;
        return AttributeType.fromValue(value);
    }
}
