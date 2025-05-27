package sora.com.saleapi.controller.csv;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sora.com.saleapi.entity.csv.Empleado;
import sora.com.saleapi.service.EmpleadoService;

import java.util.List;

@RestController
@RequestMapping("api/v1/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @PostMapping("/cargar")
    public ResponseEntity<List<Empleado>> cargar(@RequestParam ("archivo") MultipartFile archivo) {
        try{
            List<Empleado> empleadosGuardados =  empleadoService.saveCsv(archivo);
            return ResponseEntity.ok(empleadosGuardados);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
