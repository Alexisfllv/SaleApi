package sora.com.saleapi.service.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sora.com.saleapi.entity.json.Persona;
import sora.com.saleapi.repo.json.PersonaRepository;
import sora.com.saleapi.service.PersonaService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository personaRepository;


    @Override
    public List<Persona> cargarJson() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        InputStream input = getClass().getResourceAsStream("/datos.json");
        List<Persona> personas = Arrays.asList(mapper.readValue(input, Persona[].class));

        return personaRepository.saveAll(personas);
    }
}
