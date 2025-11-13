package com.jhomilmotors.jhomilwebapp.converter;
import com.jhomilmotors.jhomilwebapp.enums.ExportJobType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ExportJobTypeConverter implements AttributeConverter<ExportJobType, String> {
    @Override
    public String convertToDatabaseColumn(ExportJobType type) {
        if (type == null) return null;
        return type.getValue();
    }

    @Override
    public ExportJobType convertToEntityAttribute(String value) {
        if (value == null) return null;
        return ExportJobType.fromValue(value);
    }
}