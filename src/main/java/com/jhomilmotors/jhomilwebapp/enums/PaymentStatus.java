package com.jhomilmotors.jhomilwebapp.enums;

public enum PaymentStatus {
    PENDIENTE("pendiente"),
    CONFIRMADO("confirmado"),
    RECHAZADO("rechazado");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de pago desconocido: " + value);
    }

}
