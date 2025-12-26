package com.bezkoder.spring.security.jwt.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_options")
@Getter @Setter
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionType;   // size, milk, sugar, etc.
    private String optionValue;  // small, soy, no sugar...

    @Column(name = "price_modifier")
    private BigDecimal priceModifier;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
