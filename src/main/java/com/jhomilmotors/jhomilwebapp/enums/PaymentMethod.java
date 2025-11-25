package com.jhomilmotors.jhomilwebapp.enums;

public enum PaymentMethod {
    YAPE("yape"),
    PLIN("plin"),
    TRANSFERENCIA("transferencia"),
    CONTRAENTREGA("contraentrega"),
    POS("pos"),
    MERCADO_PAGO("mercado_pago");
    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.getValue().equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Método de pago desconocido: " + value);
    }
}
