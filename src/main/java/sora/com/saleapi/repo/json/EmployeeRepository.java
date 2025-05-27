package sora.com.saleapi.repo.json;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.json.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
