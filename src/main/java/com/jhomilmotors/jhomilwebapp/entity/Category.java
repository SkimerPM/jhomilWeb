package com.jhomilmotors.jhomilwebapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "core_categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "slug", length = 150, nullable = false, unique = true)
    private String slug;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Relación autoreferencial para subcategorías
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_padre_id")
    private Category padre;

    // Relación inversa para subcategorías
    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = false)
    private java.util.List<Category> subcategorias;


}