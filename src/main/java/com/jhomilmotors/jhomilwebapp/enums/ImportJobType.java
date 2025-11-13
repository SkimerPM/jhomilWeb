package com.jhomilmotors.jhomilwebapp.enums;

public enum ImportJobType {
    PRODUCTOS("productos"),
    LOTES("lotes"),
    COMPRAS("compras"),
    CLIENTES("clientes");
    private final String value;

    ImportJobType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ImportJobType fromValue(String value) {
        for (ImportJobType type : ImportJobType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de importación desconocido: " + value);
    }

}
