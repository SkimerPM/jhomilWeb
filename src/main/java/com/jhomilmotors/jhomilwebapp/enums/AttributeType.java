package com.jhomilmotors.jhomilwebapp.enums;

import lombok.Getter;
@Getter
public enum AttributeType {
    TEXTO("texto"),
    NUMERO("numero"),
    DECIMAL("decimal"),
    BOOLEANO("booleano"),
    LISTA("lista");

    private final String value;

    AttributeType(String value) {
        this.value = value;
    }

    public static AttributeType fromValue(String value) {
        for (AttributeType type : AttributeType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de atributo desconocido: " + value);
    }

}
