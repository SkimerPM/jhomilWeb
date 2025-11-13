package com.jhomilmotors.jhomilwebapp.enums;

public enum PurchaseStatus {
    PENDIENTE("pendiente"),
    RECIBIDO("recibido"),
    CANCELADO("cancelado");

    private final String value;

    PurchaseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PurchaseStatus fromValue(String value) {
        for (PurchaseStatus status : PurchaseStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de compra desconocido: " + value);
    }

}
