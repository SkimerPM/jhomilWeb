package com.jhomilmotors.jhomilwebapp.enums;

public enum DiscountType {
    PORCENTAJE("porcentaje"),
    MONTO_FIJO("monto_fijo"),
    DOS_POR_UNO("x_por_y");

    private final String value;

    DiscountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
