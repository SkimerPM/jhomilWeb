package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.ExportJobType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_exportjob")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Column(name = "tipo", length = 50, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.ExportJobTypeConverter.class)
    private ExportJobType tipo;

    @Column(name = "parametros", columnDefinition = "TEXT")
    private String parametros;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "url_archivo")
    private String urlArchivo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;
}