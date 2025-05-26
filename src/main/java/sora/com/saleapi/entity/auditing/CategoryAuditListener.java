package sora.com.saleapi.entity.auditing;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import sora.com.saleapi.Aut.SpringContext;
import sora.com.saleapi.entity.Category;
import sora.com.saleapi.repo.auditing.CategoryAuditLogRepo;

import java.time.LocalDateTime;

public class CategoryAuditListener {

    @PostPersist
    public void prePersist(Category category) {
        saveAudit(category, "CREATE");
    }

    @PreUpdate
    public void preUpdate(Category category) {
        saveAudit(category, "UPDATE");
    }

    @PreRemove
    public void preRemove(Category category) {
        saveAudit(category, "DELETE");
    }

    private void saveAudit(Category category, String action) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(category);

            CategoryAuditLog log = new CategoryAuditLog();
            log.setCategoryId(category.getCategoryId());
            log.setAction(action);
            log.setTimestamp(LocalDateTime.now());
            log.setDataSnapshot(json);

            // Solución para acceder a un bean dentro del listener:
            SpringContext.getBean(CategoryAuditLogRepo.class).save(log);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar auditoría", e);
        }
    }
}
