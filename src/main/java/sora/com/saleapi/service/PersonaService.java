package sora.com.saleapi.service;

import sora.com.saleapi.entity.json.Persona;

import java.io.IOException;
import java.util.List;

public interface PersonaService {
    List<Persona> cargarJson() throws IOException;
}
