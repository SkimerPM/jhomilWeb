package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="core_atributo")
@Data
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre")
    private String nombre;

    @Column(name="codigo")
    private String codigo;

    @Column(name="tipo")
    private String tipo;

    @Column(name="unidad")
    private String unidad;

    @Column(name="es_variacion")
    private Boolean esVariacion;

    @Column(name="orden_visual")
    private Integer ordenVisual;
}