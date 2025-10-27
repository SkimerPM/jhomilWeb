package com.jhomilmotors.jhomilwebapp.dto;

public class SearchResultDTO {
    private String tipo;      // "producto" o "variante"
    private Long id;
    private String nombre;
    private String descripcion;
    private String sku;
    private Long productoId;
    private String extra;

    public SearchResultDTO() { }

    // ESTE es el único constructor que necesitas
    public SearchResultDTO(String tipo, Long id, String nombre, String descripcion, String sku, Long productoId, String extra) {
        this.tipo = tipo;
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.sku = sku;
        this.productoId = productoId;
        this.extra = extra;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
