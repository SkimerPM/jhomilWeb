package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "core_marca") // Nombre de tabla en la DB
@Data
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(name = "imagen_logo", nullable = true)
    private String imagenLogoURL;
}