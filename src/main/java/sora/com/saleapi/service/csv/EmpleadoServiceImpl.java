package sora.com.saleapi.service.csv;


import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sora.com.saleapi.entity.csv.Empleado;
import sora.com.saleapi.repo.csv.EmpleadoRepository;
import sora.com.saleapi.service.EmpleadoService;

import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @Override
    public List<Empleado> saveCsv(MultipartFile file) throws Exception {
        CsvToBean<Empleado> csvToBean =  new CsvToBeanBuilder<Empleado>(
                new InputStreamReader(file.getInputStream()))
                .withType(Empleado.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withThrowExceptions(false)  // importante para no fallar al primer error
                .build();

        List<Empleado> empleados = csvToBean.parse();

        // Capturar errores de parseo
        List<CsvException> excepciones = csvToBean.getCapturedExceptions();
        for (CsvException ex : excepciones) {
            System.out.println("Error en fila " + ex.getLineNumber() + ": " + ex.getMessage());
        }

        return empleadoRepository.saveAll(empleados);
    }
}

