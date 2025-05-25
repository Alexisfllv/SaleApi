package sora.com.saleapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
}
