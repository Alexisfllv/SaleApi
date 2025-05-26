package sora.com.saleapi.entity;


import jakarta.persistence.*;
import lombok.*;
import sora.com.saleapi.entity.auditing.CategoryAuditListener;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(CategoryAuditListener.class)
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long categoryId;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @Column(name = "category_description", nullable = false, length = 250)
    private String categoryDescription;

    @Column(name = "category_enabled", nullable = false)
    private Boolean categoryEnabled;
}
