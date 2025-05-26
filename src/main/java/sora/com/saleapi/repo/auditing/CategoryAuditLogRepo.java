package sora.com.saleapi.repo.auditing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.auditing.CategoryAuditLog;

@Repository
public interface CategoryAuditLogRepo extends JpaRepository<CategoryAuditLog, Long> {
}

