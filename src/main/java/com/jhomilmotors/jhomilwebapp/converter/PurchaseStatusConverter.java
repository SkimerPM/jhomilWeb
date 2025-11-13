package com.jhomilmotors.jhomilwebapp.converter;

import com.jhomilmotors.jhomilwebapp.enums.PurchaseStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PurchaseStatusConverter implements AttributeConverter<PurchaseStatus, String> {
    @Override
    public String convertToDatabaseColumn(PurchaseStatus status) {
        if (status == null) return null;
        return status.getValue();
    }

    @Override
    public PurchaseStatus convertToEntityAttribute(String value) {
        if (value == null) return null;
        return PurchaseStatus.fromValue(value);
    }
}