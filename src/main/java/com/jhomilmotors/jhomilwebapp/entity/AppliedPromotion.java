package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "core_promocionaplicada")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Order pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promocion_id")
    private Promotion promocion;

    @Column(name = "nombre_snapshot", length = 150, nullable = false)
    private String nombreSnapshot;

    @Column(name = "valor_descuento_aplicado", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorDescuentoAplicado;
}
