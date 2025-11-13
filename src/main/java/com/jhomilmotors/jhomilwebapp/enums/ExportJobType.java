package com.jhomilmotors.jhomilwebapp.enums;

public enum ExportJobType {

    VENTAS("ventas"),
    STOCK("stock"),
    PRODUCTOS("productos");
    private final String value;

    ExportJobType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExportJobType fromValue(String value) {
        for (ExportJobType type : ExportJobType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de exportación desconocido: " + value);
    }

}
