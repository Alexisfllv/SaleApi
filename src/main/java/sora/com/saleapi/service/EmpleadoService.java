package sora.com.saleapi.service;

import org.springframework.web.multipart.MultipartFile;
import sora.com.saleapi.entity.csv.Empleado;

import java.util.List;

public interface EmpleadoService {
    List<Empleado> saveCsv(MultipartFile file)throws Exception;
}
