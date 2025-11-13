package com.jhomilmotors.jhomilwebapp.converter;


import com.jhomilmotors.jhomilwebapp.enums.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String>{
    @Override
    public String convertToDatabaseColumn(OrderStatus status) {
        if (status == null) return null;
        return status.getValue();
    }
    @Override
    public OrderStatus convertToEntityAttribute(String value) {
        if (value == null) return null;
        return OrderStatus.fromValue(value);
    }

}
