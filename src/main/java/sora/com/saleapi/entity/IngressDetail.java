package sora.com.saleapi.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(
        name = "ingress_detail",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ingress_id", "product_id"})
        }
)
public class IngressDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long ingressDetailId;

    @Column(name = "ingress_detail_product_id", nullable = false)
    private Integer ingressDetailQuantity;

    @Column(name = "ingress_detail_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal ingressDetailPrice;

    // fks

    @ManyToOne
    @JoinColumn(name = "ingress_id", nullable = false)
    private Ingress ingress;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
