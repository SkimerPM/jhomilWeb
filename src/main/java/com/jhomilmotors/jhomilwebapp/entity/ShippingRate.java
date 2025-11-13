package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_tarifaenvio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_id", nullable = false)
    private City ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private ShippingCompany empresa;

    @Column(name = "peso_min_kg", precision = 8, scale = 2)
    private BigDecimal pesoMinKg;

    @Column(name = "peso_max_kg", precision = 8, scale = 2)
    private BigDecimal pesoMaxKg;

    @Column(name = "costo", precision = 12, scale = 2, nullable = false)
    private BigDecimal costo;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}