package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.InventoryMovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_movimientoinventario")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Batch lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private ProductVariant variante;

    @Column(name = "tipo", length = 20, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.InventoryMovementTypeConverter.class)
    private InventoryMovementType tipo;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "saldo_despues")
    private Integer saldoDespues;

    @Column(name = "costo_unitario", precision = 12, scale = 4)
    private BigDecimal costoUnitario;

    @Column(name = "total_costo", precision = 12, scale = 2)
    private BigDecimal totalCosto;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @Column(name = "fecha")
    private LocalDateTime fecha = LocalDateTime.now();
}
