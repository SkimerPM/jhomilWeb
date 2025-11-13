package com.jhomilmotors.jhomilwebapp.enums;

public enum InventoryMovementType {
    ENTRADA("entrada"),
    SALIDA("salida"),
    AJUSTE("ajuste"),
    RESERVA("reserva"),
    DEVOLUCION("devolucion");
    private final String value;

    InventoryMovementType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static InventoryMovementType fromValue(String value) {
        for (InventoryMovementType type : InventoryMovementType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de movimiento desconocido: " + value);
    }

}
