package com.jhomilmotors.jhomilwebapp.entity;
import com.jhomilmotors.jhomilwebapp.enums.ReceiptStatus;
import com.jhomilmotors.jhomilwebapp.enums.ReceiptType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_comprobante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Order pedido;

    @Column(name = "tipo", length = 20, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.ReceiptTypeConverter.class)
    private ReceiptType tipo;

    @Column(name = "numero", length = 50, nullable = false, unique = true)
    private String numero;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Column(name = "monto_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "impuesto", precision = 12, scale = 2, nullable = false)
    private BigDecimal impuesto;

    @Column(name = "pdf_url", length = 1024)
    private String pdfUrl;

    @Column(name = "estado", length = 20, nullable = false)
    @Convert(converter = com.jhomilmotors.jhomilwebapp.converter.ReceiptStatusConverter.class)
    private ReceiptStatus estado = ReceiptStatus.EMITIDA;
}