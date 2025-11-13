package com.jhomilmotors.jhomilwebapp.enums;

public enum ReceiptType {
    BOLETA("boleta"),
    FACTURA("factura");

    private final String value;

    ReceiptType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReceiptType fromValue(String value) {
        for (ReceiptType type : ReceiptType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de comprobante desconocido: " + value);
    }

}
