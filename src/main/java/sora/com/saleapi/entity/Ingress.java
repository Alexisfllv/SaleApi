package sora.com.saleapi.entity;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString(exclude = {"provider","user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "ingress")
public class Ingress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long ingressId;

    @Column(name = "ingress_tax", nullable = false,precision = 10, scale = 2)
    private BigDecimal ingressTax;

    @Column(name = "ingress_total", nullable = false,precision = 10, scale = 2)
    private BigDecimal ingressTotal;

    @Column(name = "ingress_date_time", nullable = false)
    private LocalDateTime ingressDateTime;

    @Column(name = "ingress_serial_number", nullable = false, length = 250)
    private String ingressSerialNumber;

    @Column(name = "ingress_enabled", nullable = false)
    private Boolean ingressEnabled;

    //fks

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
