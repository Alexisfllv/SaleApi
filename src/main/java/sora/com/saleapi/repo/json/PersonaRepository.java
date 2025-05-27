package sora.com.saleapi.repo.json;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sora.com.saleapi.entity.json.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
