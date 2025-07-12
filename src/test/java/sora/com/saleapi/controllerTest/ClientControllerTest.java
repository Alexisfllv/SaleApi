package sora.com.saleapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sora.com.saleapi.controller.ClientController;
import sora.com.saleapi.dto.ClientDTO.ClientDTORequest;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.service.ClientService;

// static
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @MockitoBean
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

        clientDTOResponse = new ClientDTOResponse(1L, "Juan", "Pérez", "juan.perez@example.com", "12345678", "987654321", "Av. Siempre Viva 742");

        clientDTOResponse2 = new ClientDTOResponse(2L, "Ana", "García", "ana.garcia@example.com", "87654321", "912345678", "Calle Falsa 123");

        clientDTOResponseList = List.of(clientDTOResponse, clientDTOResponse2);
    }


    public static final String MESSAGE_CREATED = "Creado correctamente";
    public static final String MESSAGE_UPDATED = "Actualizado correctamente";
    public static final String MESSAGE_DELETED = "Eliminado correctamente";
    public static final String MESSAGE_NOT_FOUND = "Client not found";
    public static final String ERROR_REQUIRED = "This field is required";
    public static final String ERROR_INVALID_FORMAT = "Invalid format";
    public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";
    public static final String ERROR_SIZE_DESCRIPTION = "The number of items must be between 2 and 250";

    @Nested
    @DisplayName("GET/clients")
    class GetClientTests {

        // list
        @Test
        @DisplayName("should return full list of clients")
        public void shouldReturnFullListOfClients()throws Exception {
            // Arrange
            when(clientService.findAll()).thenReturn(clientDTOResponseList);
            // Act & Assert
            mockMvc.perform(get(APICLIENT))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].clientId").value(1L))
                    .andExpect(jsonPath("$[0].clientFirstName").value("Juan"))
                    .andExpect(jsonPath("$[1].clientId").value(2L))
                    .andExpect(jsonPath("$[1].clientFirstName").value("Ana"));
            // Verify
            verify(clientService, times(1)).findAll();
        }

        // list paged
        @Test
        @DisplayName("should return paginated list of clients")
        public void shouldReturnPaginatedListOfClients() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("clientId").ascending());
            Page<ClientDTOResponse> pageClient = new PageImpl<>(clientDTOResponseList, pageable, clientDTOResponseList.size());

            when(clientService.findAllPage(any(Pageable.class))).thenReturn(pageClient);
            // Act & Assert
            mockMvc.perform(get(APICLIENT+ "/page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "clientId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].clientId").value(1L))
                    .andExpect(jsonPath("$.content[0].clientFirstName").value("Juan"))
                    .andExpect(jsonPath("$.content[1].clientId").value(2L))
                    .andExpect(jsonPath("$.content[1].clientFirstName").value("Ana"));

            // Verify
            verify(clientService, times(1)).findAllPage(any(Pageable.class));
        }

        // list page empy
        @Test
        @DisplayName("should return empty paginated list of clients")
        public void shouldReturnEmptyPaginatedListOfClients() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("clientId").ascending());
            Page<ClientDTOResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable,0);
            when(clientService.findAllPage(any(Pageable.class))).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(APICLIENT + "/page")
                            .param("page", "0")
                            .param("size", "5")
                            .param("sort", "clientId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(0)));

            // Verify
            verify(clientService, times(1)).findAllPage(any(Pageable.class));
        }

        // find by id
        @Test
        @DisplayName("shouldd return client by ID")
        public void shoulddReturnClientByID() throws Exception {
            // Arrange
            Long clientId = 1L;
            when(clientService.findById(clientId)).thenReturn(clientDTOResponse);

            // Act & Assert
            mockMvc.perform(get(APICLIENT + "/"+ clientId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.clientId").value(clientId))
                    .andExpect(jsonPath("$.clientFirstName").value("Juan"))
                    .andExpect(jsonPath("$.clientLastName").value("Pérez"))
                    .andExpect(jsonPath("$.clientEmail").value("juan.perez@example.com"))
                    .andExpect(jsonPath("$.clientCardId").value("12345678"))
                    .andExpect(jsonPath("$.clientPhone").value("987654321"))
                    .andExpect(jsonPath("$.clientAddress").value("Av. Siempre Viva 742"));

            //(1L, "Juan", "Pérez", "juan.perez@example.com", "12345678", "987654321", "Av. Siempre Viva 742");

            // Verify
            verify(clientService, times(1)).findById(clientId);
        }
        // findByIdFail
        @Test
        @DisplayName("should return 404 when client ID does not exist")
        public void shoulddReturn404WhenClientIDDoesNotExist() throws Exception {
            // Arrange
            Long clientIdNonExist = 1L;
            when(clientService.findById(clientIdNonExist)).thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));

            // Act & Assert
            mockMvc.perform(get(APICLIENT + "/"+ clientIdNonExist))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.path").value(APICLIENT + "/"+ clientIdNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verify(clientService, times(1)).findById(clientIdNonExist);
        }
    }

}
