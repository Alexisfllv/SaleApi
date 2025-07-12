package sora.com.saleapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import sora.com.saleapi.controller.ClientController;
import sora.com.saleapi.dto.ClientDTO.ClientDTORequest;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.service.ClientService;

// static
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    // var
    private ClientDTORequest clientDTORequest;

    private ClientDTOResponse clientDTOResponse;
    private ClientDTOResponse clientDTOResponse2;
    private List<ClientDTOResponse> clientDTOResponseList;

    private String APICLIENT = "/api/v1/clients";

    @BeforeEach
    public void setup() {
        clientDTORequest = new ClientDTORequest("Juan", "Pérez", "juan.perez@example.com", "12345678", "987654321", "Av. Siempre Viva 742");

        clientDTOResponse = new ClientDTOResponse(1L, "Juan", "Pérez", "juan.perez@example.com", "12345678", "987654321", "Av. Siempre Viva 742", true);

        clientDTOResponse2 = new ClientDTOResponse(2L, "Ana", "García", "ana.garcia@example.com", "87654321", "912345678", "Calle Falsa 123", true);

        clientDTOResponseList = List.of(clientDTOResponse, clientDTOResponse2);
    }

    public class TestConstants {
        public static final String MESSAGE_CREATED = "Creado correctamente";
        public static final String MESSAGE_UPDATED = "Actualizado correctamente";
        public static final String MESSAGE_DELETED = "Eliminado correctamente";
        public static final String MESSAGE_NOT_FOUND = "Client not found";
        public static final String ERROR_REQUIRED = "This field is required";
        public static final String ERROR_INVALID_FORMAT = "Invalid format";
        public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";
        public static final String ERROR_SIZE_DESCRIPTION = "The number of items must be between 2 and 250";
    }

}
