package sora.com.saleapi.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long clientId;

    @Column(name = "client_first_name",nullable = false,length = 50)
    private String clientFirstName;

    @Column(name = "client_last_name",nullable = false,length = 50)
    private String clientLastName;

    @Column(name = "client_email",nullable = false,length = 100)
    private String clientEmail;

    @Column(name = "client_card_id",nullable = false,length = 30)
    private String  clientCardId;

    @Column(name = "client_phone",nullable = false,length = 9)
    private String clientPhone;

    @Column(name = "client_address",nullable = false,length = 100)
    private String clientAddress;
}
