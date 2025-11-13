package com.jhomilmotors.jhomilwebapp.converter;
import com.jhomilmotors.jhomilwebapp.enums.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter(autoApply = true)
public class PaymentStatusConverter  implements AttributeConverter<PaymentStatus, String>  {
    @Override
    public String convertToDatabaseColumn(PaymentStatus status) {
        if (status == null) return null;
        return status.getValue();
    }
    @Override
    public PaymentStatus convertToEntityAttribute(String value) {
        if (value == null) return null;
        return PaymentStatus.fromValue(value);
    }
}
