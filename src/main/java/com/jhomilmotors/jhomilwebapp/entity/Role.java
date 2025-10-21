package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "core_rol")
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 20)
    private RoleName nombre;

    private String descripcion;
    public Role(RoleName nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}