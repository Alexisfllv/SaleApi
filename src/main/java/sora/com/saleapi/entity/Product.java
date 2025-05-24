package sora.com.saleapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "category")
@ToString(exclude = "category")
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "product_name",nullable = false,length = 50)
    private String productName;

    @Column(name = "product_description",nullable = false, length = 250)
    private String productDescription;

    @Column(name = "product_price",nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "product_enabled",nullable = false)
    private Boolean productEnabled;

    // fk
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
