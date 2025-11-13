package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "core_imagen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Product product;
    //Nueva relación a variante(puede ser nula)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    private ProductVariant variante;
    @Column(name = "url", length = 1024, nullable = false)
    private String url;
    @Column(name = "es_principal")
    private Boolean esPrincipal = false;
    @Column(name = "orden")
    private Integer orden = 0;
}