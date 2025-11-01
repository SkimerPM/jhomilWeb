package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_promocionproducto")
public class PromotionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "promocion_id", nullable = false)
    private Promotion promocion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = true)
    private Product producto;

    @ManyToOne
    @JoinColumn(name = "variante_id", nullable = true)
    private ProductVariant variante;

    @ManyToOne
    @JoinColumn(name = "producto_gratis_id", nullable = true)
    private Product productoGratis;

    @ManyToOne
    @JoinColumn(name = "variante_gratis_id", nullable = true)
    private ProductVariant varianteGratis;

    // **Aquí sí van**
    @Column(nullable = false)
    private Integer cantidadRequerida = 1;  // ej: “compra 2”

    @Column(nullable = false)
    private Integer cantidadGratis = 1;     // ej: “lleva 1”

    public PromotionProduct() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Promotion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promotion promocion) {
        this.promocion = promocion;
    }

    public Product getProducto() {
        return producto;
    }

    public void setProducto(Product producto) {
        this.producto = producto;
    }

    public ProductVariant getVariante() {
        return variante;
    }

    public void setVariante(ProductVariant variante) {
        this.variante = variante;
    }

    public Product getProductoGratis() {
        return productoGratis;
    }

    public void setProductoGratis(Product productoGratis) {
        this.productoGratis = productoGratis;
    }

    public ProductVariant getVarianteGratis() {
        return varianteGratis;
    }

    public void setVarianteGratis(ProductVariant varianteGratis) {
        this.varianteGratis = varianteGratis;
    }

    public Integer getCantidadRequerida() {
        return cantidadRequerida;
    }

    public void setCantidadRequerida(Integer cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }

    public Integer getCantidadGratis() {
        return cantidadGratis;
    }

    public void setCantidadGratis(Integer cantidadGratis) {
        this.cantidadGratis = cantidadGratis;
    }
}
