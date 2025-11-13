package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "core_empresaenvio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "api_endpoint")
    private String apiEndpoint;

    // Relación inversa
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ShippingRate> tarifas;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Shipment> envios;
}