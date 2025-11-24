package com.jhomilmotors.jhomilwebapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PromotionDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String tipoDescuento;
    private BigDecimal valorDescuento;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean activo;
    private BigDecimal minCompra;
    private Integer maxUsos;
    private Long varianteGratisId;
    private Integer cantidadGratis;

    public PromotionDTO() {
    }

    public Long getVarianteGratisId() {
        return varianteGratisId;
    }

    public void setVarianteGratisId(Long varianteGratisId) {
        this.varianteGratisId = varianteGratisId;
    }

    public Integer getCantidadGratis() {
        return cantidadGratis;
    }

    public void setCantidadGratis(Integer cantidadGratis) {
        this.cantidadGratis = cantidadGratis;
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

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
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
