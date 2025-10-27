package com.jhomilmotors.jhomilwebapp.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="core_productoatributo")
@Data
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="producto_id")
    private Product product;

    @ManyToOne @JoinColumn(name="atributo_id")
    private Attribute attribute;

    @Column(name="valor_text")
    private String valorText;

    @Column(name="valor_num")
    private java.math.BigDecimal valorNum;
}