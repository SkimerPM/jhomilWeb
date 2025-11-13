package com.jhomilmotors.jhomilwebapp.converter;
import com.jhomilmotors.jhomilwebapp.enums.ReceiptStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


public class ReceiptStatusConverter  implements AttributeConverter<ReceiptStatus, String> {

    @Override
    public String convertToDatabaseColumn(ReceiptStatus status) {
        if (status == null) return null;
        return status.getValue();
    }
    @Override
    public ReceiptStatus convertToEntityAttribute(String value) {
        if (value == null) return null;
        return ReceiptStatus.fromValue(value);
    }


}
