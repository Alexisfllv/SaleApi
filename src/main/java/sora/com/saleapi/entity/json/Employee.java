package sora.com.saleapi.entity.json;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Employee {
    @Id
    private Long id;

    private String nombre;
    private String apellido;
    private String correo;

    private LocalDate fechaNacimiento;

    private BigDecimal salario;
    private boolean activo;
}
