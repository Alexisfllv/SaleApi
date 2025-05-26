package sora.com.saleapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.Sale;

@Repository
public interface SaleRepo extends JpaRepository<Sale, Long> {
}
