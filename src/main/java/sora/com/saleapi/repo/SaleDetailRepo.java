package sora.com.saleapi.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.SaleDetail;

@Repository
public interface SaleDetailRepo extends JpaRepository<SaleDetail, Long> {
}
