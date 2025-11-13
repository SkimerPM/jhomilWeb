package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.DiscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "core_promocion")
@Data
@AllArgsConstructor

public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "codigo", length = 50, unique = true)
    private String codigo;

    @Column(name = "tipo_descuento", length = 20, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.DiscountTypeConverter.class)
    private DiscountType tipoDescuento;

    @Column(name = "valor_descuento", precision = 8, scale = 2, nullable = false)
    private BigDecimal valorDescuento = BigDecimal.ZERO;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "min_compra", precision = 12, scale = 2)
    private BigDecimal minCompra;

    @Column(name = "max_usos")
    private Integer maxUsos;

    // Relación inversa
    @OneToMany(mappedBy = "promocion", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<PromotionProduct> productos;


    public Promotion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public DiscountType getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(DiscountType tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public BigDecimal getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(BigDecimal valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getMinCompra() {
        return minCompra;
    }

    public void setMinCompra(BigDecimal minCompra) {
        this.minCompra = minCompra;
    }

    public Integer getMaxUsos() {
        return maxUsos;
    }

    public void setMaxUsos(Integer maxUsos) {
        this.maxUsos = maxUsos;
    }
}
