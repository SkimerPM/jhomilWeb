package com.jhomilmotors.jhomilwebapp.enums;

public enum ReceiptStatus {
    EMITIDA("emitida"),
    ANULADA("anulada");
    private final String value;

    ReceiptStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReceiptStatus fromValue(String value) {
        for (ReceiptStatus status : ReceiptStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de comprobante desconocido: " + value);
    }

}
