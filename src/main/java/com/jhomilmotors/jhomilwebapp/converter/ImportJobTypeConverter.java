package com.jhomilmotors.jhomilwebapp.converter;
import com.jhomilmotors.jhomilwebapp.enums.ImportJobType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public class ImportJobTypeConverter implements AttributeConverter<ImportJobType, String> {

    @Override
    public String convertToDatabaseColumn(ImportJobType type) {
        if (type == null) return null;
        return type.getValue();
    }
    @Override
    public ImportJobType convertToEntityAttribute(String value) {
        if (value == null) return null;
        return ImportJobType.fromValue(value);
    }

}
