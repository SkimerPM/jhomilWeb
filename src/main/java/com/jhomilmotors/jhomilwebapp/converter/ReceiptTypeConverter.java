package com.jhomilmotors.jhomilwebapp.converter;

import com.jhomilmotors.jhomilwebapp.enums.ReceiptType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter(autoApply = true)
public class ReceiptTypeConverter implements AttributeConverter<ReceiptType, String> {

    @Override
    public String convertToDatabaseColumn(ReceiptType type) {
        if (type == null) return null;
        return type.getValue();
    }

    @Override
    public ReceiptType convertToEntityAttribute(String value) {
        if (value == null) return null;
        return ReceiptType.fromValue(value);
    }


}
