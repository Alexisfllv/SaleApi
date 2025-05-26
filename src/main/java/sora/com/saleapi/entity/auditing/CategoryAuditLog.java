package sora.com.saleapi.entity.auditing;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "category_audit_log")
public class CategoryAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long categoryId;

    private String action; // CREATE, UPDATE, DELETE

    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String dataSnapshot;
}
