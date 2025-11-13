package com.jhomilmotors.jhomilwebapp.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "core_ciudad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    // Relación inversa
    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ShippingRate> tarifas;

    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Shipment> envios;
}