package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.PaymentMethod;
import com.jhomilmotors.jhomilwebapp.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_pago")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Order pedido;

    @Column(name = "metodo", length = 20, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.PaymentMethodConverter.class)
    private PaymentMethod metodo;

    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    @Builder.Default
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.PaymentStatusConverter.class)
    private PaymentStatus estado = PaymentStatus.PENDIENTE;

    @Column(name = "comprobante_url", length = 1024)
    private String comprobanteUrl;

    @Column(name = "referencia_externa", length = 255)
    private String referenciaExterna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_verificador_id")
    private User usuarioVerificador;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;
}