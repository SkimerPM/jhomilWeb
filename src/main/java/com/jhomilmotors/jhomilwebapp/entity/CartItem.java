package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "core_carritoitem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Cart carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private ProductVariant variante;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_snapshot", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitarioSnapshot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCarrito() {
        return carrito;
    }

    public void setCarrito(Cart carrito) {
        this.carrito = carrito;
    }

    public ProductVariant getVariante() {
        return variante;
    }

    public void setVariante(ProductVariant variante) {
        this.variante = variante;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioSnapshot() {
        return precioUnitarioSnapshot;
    }

    public void setPrecioUnitarioSnapshot(BigDecimal precioUnitarioSnapshot) {
        this.precioUnitarioSnapshot = precioUnitarioSnapshot;
    }
}