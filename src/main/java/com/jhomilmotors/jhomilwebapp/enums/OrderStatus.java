package com.jhomilmotors.jhomilwebapp.enums;

public enum OrderStatus {
    PENDIENTE("pendiente"),
    PAGADO("pagado"),
    PREPARANDO("preparando"),
    ENVIADO("enviado"),
    ENTREGADO("entregado"),
    CANCELADO("cancelado");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de pedido desconocido: " + value);
    }

}
