package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "core_region")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    // Relación inversa
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<City> ciudades;
}
