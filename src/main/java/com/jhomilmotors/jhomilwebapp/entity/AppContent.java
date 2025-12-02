package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "core_app_content")
@Getter @Setter
public class AppContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El código único (ej: "TYC", "PRIVACIDAD")
    @Column(unique = true, nullable = false)
    private String codigo;

    // El contenido HTML guardado como texto largo
    @Column(columnDefinition = "TEXT", nullable = false)
    private String htmlContent;
}