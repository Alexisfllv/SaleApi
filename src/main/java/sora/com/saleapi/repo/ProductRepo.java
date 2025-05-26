package sora.com.saleapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
}
