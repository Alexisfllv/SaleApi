package sora.com.saleapi.controller.json;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sora.com.saleapi.entity.json.Persona;
import sora.com.saleapi.service.PersonaService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping("/cargar")
    public ResponseEntity<List<Persona>> cargarPersona()throws IOException {
        List<Persona> lista =  personaService.cargarJson();
        return ResponseEntity.ok(lista);

    }
}
