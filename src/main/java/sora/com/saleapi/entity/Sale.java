package sora.com.saleapi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"client","user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long saleId;

    @Column(name = "sale_total",nullable = true)
    private BigDecimal saleTotal;

    @Column(name = "sale_tax",nullable = true)
    private BigDecimal saleTax;

    @Column(name = "sale_enabled", nullable = false)
    private Boolean saleEnabled;

    // fks
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // list Sale Detail

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetail> details = new ArrayList<>();

}
