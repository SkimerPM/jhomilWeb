package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.AttributeType;
import com.jhomilmotors.jhomilwebapp.converter.AttributeTypeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="core_atributo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "codigo", length = 100, nullable = false, unique = true)
    private String codigo;

    @Column(name = "tipo", length = 20, nullable = false)
    @Convert(converter = AttributeTypeConverter.class)
    private AttributeType tipo;

    @Column(name = "unidad", length = 50)
    private String unidad;

    @Column(name = "es_variacion")
    private Boolean esVariacion = false;

    @Column(name = "orden_visual")
    private Integer ordenVisual = 0;

}