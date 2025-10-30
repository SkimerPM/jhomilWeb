package com.jhomilmotors.jhomilwebapp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "core_proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 20)
    private String ruc;

    @Column(length = 200)
    private String contacto;

    @Column(length = 50)
    private String telefono;

    @Column
    private String email;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    // Relación con compras
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Purchase> compras;

    // Relación con lotes
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Batch> lotes;
}
