package com.jhomilmotors.jhomilwebapp.entity;
import com.jhomilmotors.jhomilwebapp.enums.ImportJobType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_importjob")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Column(name = "tipo", length = 50, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.ImportJobTypeConverter.class)
    private ImportJobType tipo;

    @Column(name = "archivo_url", nullable = false)
    private String archivoUrl;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "errores", columnDefinition = "TEXT")
    private String errores;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
}
