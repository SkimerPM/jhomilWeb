package com.jhomilmotors.jhomilwebapp.enums;

public enum ShipmentStatus {
    PENDIENTE("pendiente"),
    EN_TRANSITO("en_transito"),
    ENTREGADO("entregado"),
    DEVUELTO("devuelto");

    private final String value;

    ShipmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ShipmentStatus fromValue(String value) {
        for (ShipmentStatus status : ShipmentStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de envío desconocido: " + value);
    }

}
