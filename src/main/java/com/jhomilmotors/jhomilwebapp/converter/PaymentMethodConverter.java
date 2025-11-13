package com.jhomilmotors.jhomilwebapp.converter;
import com.jhomilmotors.jhomilwebapp.enums.PaymentMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {
        @Override
        public String convertToDatabaseColumn(PaymentMethod method) {
            if (method == null) return null;
            return method.getValue();
        }
        @Override
        public PaymentMethod convertToEntityAttribute(String value) {
            if (value == null) return null;
            return PaymentMethod.fromValue(value);
        }

}
