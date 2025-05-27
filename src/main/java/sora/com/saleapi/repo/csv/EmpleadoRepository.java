package sora.com.saleapi.repo.csv;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.csv.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}