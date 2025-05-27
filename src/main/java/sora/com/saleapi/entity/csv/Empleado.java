package sora.com.saleapi.entity.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Empleado {

    @Id
    @CsvBindByName(column = "ID")
    private Long id;

    @CsvBindByName(column = "Nombre")
    @NotBlank
    private String nombre;

    @CsvBindByName(column = "Apellido")
    private String apellido;

    @CsvBindByName(column = "Correo")
    @Email
    private String correo;

    @CsvBindByName(column = "FechaNacimiento")
    @CsvDate("yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @CsvBindByName(column = "Salario")
    private BigDecimal salario;

    @CsvBindByName(column = "Activo")
    private boolean activo;

}