package com.jhomilmotors.jhomilwebapp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="core_productoatributo", uniqueConstraints = {
@UniqueConstraint(columnNames = {"producto_id", "atributo_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atributo_id", nullable = false)
    private Attribute attribute;

    @Column(name = "valor_text", columnDefinition = "TEXT")
    private String valorText;

    @Column(name = "valor_num", precision = 12, scale = 4)
    private BigDecimal valorNum;

}