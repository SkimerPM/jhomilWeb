package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "core_categoria") // ¡Ajusta este nombre de tabla si es diferente!
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String slug;
    private String descripcion;

    // Si tu tabla de Django usa un campo id_padre numérico simple, mapea eso.
    // Si necesitas el objeto completo, usa @ManyToOne.
    // @ManyToOne
    // @JoinColumn(name = "id_padre")
    // private Category parent;
}