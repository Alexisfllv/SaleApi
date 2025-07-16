package sora.com.saleapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
        clientDTORequest = new ClientDTORequest("Juan", "Pérez", "juan.perez@example.com", "ID12345699", "987654321", "Av. Siempre Viva 742");

        clientDTOResponse = new ClientDTOResponse(1L, "Juan", "Pérez", "juan.perez@example.com", "ID12345699", "987654321", "Av. Siempre Viva 742");

        clientDTOResponse2 = new ClientDTOResponse(2L, "Ana", "García", "ana.garcia@example.com", "ID12345693", "912345678", "Calle Falsa 123");

        clientDTOResponseList = List.of(clientDTOResponse, clientDTOResponse2);
    }


    public static final String MESSAGE_CREATED = "Creado correctamente";
    public static final String MESSAGE_UPDATED = "Actualizado correctamente";
    public static final String MESSAGE_DELETED = "Eliminado correctamente";
    public static final String MESSAGE_NOT_FOUND = "Client not found";
    public static final String ERROR_REQUIRED = "This field is required";
    public static final String ERROR_INVALID_FORMAT = "Invalid format";
    public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";
    public static final String ERROR_SIZE_EMAIL = "The number of items must be between 2 and 100";
    public static final String ERROR_EMAIL = "Invalid email address";
    public static final String ERROR_SIZE_CARD= "The number of items must be between 8 and 30";
    public static final String ERROR_SIZE_PHONE= "The number of items must be between 9 and 9";
    public static final String ERROR_SIZE_ADDRESS = "The number of items must be between 2 and 100";


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
                    .andExpect(jsonPath("$.clientCardId").value("ID12345699"))
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

    @Nested
    @DisplayName("POST/clients")
    class PostClientTest {

        // save
        @Test
        @DisplayName("should create client successfully")
        void shouldCreateClient() throws Exception {
            // Arrange
            when(clientService.save(any(ClientDTORequest.class))).thenReturn(clientDTOResponse);

            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientDTORequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.clientId").value(1L))
                    .andExpect(jsonPath("$.clientFirstName").value("Juan"))
                    .andExpect(jsonPath("$.clientLastName").value("Pérez"))
                    .andExpect(jsonPath("$.clientEmail").value("juan.perez@example.com"))
                    .andExpect(jsonPath("$.clientCardId").value("ID12345699"))
                    .andExpect(jsonPath("$.clientPhone").value("987654321"))
                    .andExpect(jsonPath("$.clientAddress").value("Av. Siempre Viva 742"));
            // Verify
            verify(clientService, times(1)).save(any(ClientDTORequest.class));
        }

        // @Valid clientFirstName
        @ParameterizedTest
        @DisplayName("should return 400 when clientFirstName is invalid")
        @MethodSource("provideInvalidClientFirstName")
        void shouldReturnValidationErrorForInvalidClientFirstName(String invalid,String expectedMessage) throws Exception {

            // Arrange
            String json = """
                    {
                        "clientFirstName": "%s",
                        "clientLastName": "Faw",
                        "clientEmail": "sass.ramirez@example.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);

            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientFirstName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoMoreInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidClientFirstName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientLastName
        @ParameterizedTest
        @DisplayName("should return 400 when clientLastName is invalid")
        @MethodSource("provideInvalidClientLastName")
        void shouldReturnValidationErrorForInvalidClientLastName(String invalid,String expectedMessage) throws Exception {

            // Arrange
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "%s",
                        "clientEmail": "sass.ramirez@example.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);

            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientLastName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoMoreInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidClientLastName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientEmail
        @ParameterizedTest
        @DisplayName("should return 400 when clientEmail is invalid")
        @MethodSource("provideInvalidClientEmail")
        void shouldReturnValidationErrorForInvalidClientEmail(String invalid,String expectedMessage) throws Exception {

            // Arrange
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Perez",
                        "clientEmail": "%s",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);

            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientEmail")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoMoreInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidClientEmail() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_EMAIL),
                    Arguments.of("@gmail.com",ERROR_EMAIL)
            );
        }

        // @Valid clientCardId
        @ParameterizedTest
        @DisplayName("should return 400 when clientCardId is invalid")
        @MethodSource("provideInvalidClientCardId")
        void shouldReturnValidationErrorForInvalidClientCardId(String invalid,String expectedMessage) throws Exception {

            // Arrange
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Perez",
                        "clientEmail": "sass.ramirez@example.com",
                        "clientCardId": "%s",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);

            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientCardId")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoMoreInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidClientCardId() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_CARD),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientPhone
        @ParameterizedTest
        @DisplayName("should return 400 when clientPhone is invalid")
        @MethodSource("provideInvalidClientPhone")
        void shouldReturnValidationErrorForInvalidClientPhone(String invalid,String expectedMessage) throws Exception {

            // Arrange
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Perez",
                        "clientEmail": "sass.ramirez@example.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "%s",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);

            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientPhone")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoMoreInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidClientPhone() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_PHONE),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientAddress
        @ParameterizedTest
        @DisplayName("should return 400 when clientAddress is invalid")
        @MethodSource("provideInvalidClientAddress")
        void shouldReturnValidationErrorForInvalidClientAddress(String invalid,String expectedMessage) throws Exception {

            // Arrange
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Perez",
                        "clientEmail": "sass.ramirez@example.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987456321",
                        "clientAddress": "%s"
                    }
                    """.formatted(invalid);

            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientAddress")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoMoreInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidClientAddress() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_ADDRESS),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // json bad format
        @Test
        @DisplayName("should return 400 when JSON is malformed")
        void shouldReturnMalformedJsonError() throws Exception {
            // Arrange
            String json = """
                    {
                        "clientFirstName": yes,
                        "clientLastName": "Perez",
                        "clientEmail": "sass.ramirez@example.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987456321",
                        "clientAddress": "Lima"
                    }
                   """;
            // Act & Assert
            mockMvc.perform(post(APICLIENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value("JSON bad format."))
                    .andExpect(jsonPath("$.path").value(APICLIENT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(clientService);
        }

    }

    @Nested
    @DisplayName("PUT/clients/{id}")
    class PutClientTest {

        // success
        @Test
        @DisplayName("should update client successfully")
        void shouldUpdateClientSuccessfully() throws Exception {
            // Arrange
            Long clientId = 1L;
            ClientDTORequest updateRequest = new ClientDTORequest("Same", "Las", "same@example.com", "87456321", "123456789", "Av. South");
            ClientDTOResponse updateResponse = new ClientDTOResponse(1L, "Same", "Las", "same@example.com", "87456321", "123456789", "Av. South");

            when(clientService.update(eq(clientId), any(ClientDTORequest.class))).thenReturn(updateResponse);
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.clientId").value(clientId))
                    .andExpect(jsonPath("$.clientFirstName").value("Same"))
                    .andExpect(jsonPath("$.clientLastName").value("Las"))
                    .andExpect(jsonPath("$.clientEmail").value("same@example.com"))
                    .andExpect(jsonPath("$.clientCardId").value("87456321"))
                    .andExpect(jsonPath("$.clientPhone").value("123456789"))
                    .andExpect(jsonPath("$.clientAddress").value("Av. South"));

            // Verify
            verify(clientService,times(1)).update(eq(clientId), any(ClientDTORequest.class));
        }

        // fail update
        @Test
        @DisplayName("should return 404 when updating non-existent client")
        void shouldReturn404WhenUpdatingNonExistentClient() throws Exception {
            // Arrange
            Long clientIdNonExist= 99L;
            ClientDTORequest updateRequest = new ClientDTORequest("Same", "Las", "same@example.com", "87456321", "123456789", "Av. South");

            when(clientService.update(eq(clientIdNonExist), any(ClientDTORequest.class))).thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));

            // Act & Assert;
            mockMvc.perform(put(APICLIENT+"/" + clientIdNonExist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientIdNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verify(clientService,times(1)).update(eq(clientIdNonExist), any(ClientDTORequest.class));
            verifyNoMoreInteractions(clientService);

        }

        // @Valid clientFirstName
        @ParameterizedTest
        @DisplayName("should return 400 when clientFirstname is invalid on update")
        @MethodSource("provideInvalidclientFirstName")
        void shouldReturn400WhenClientFirstNameIsInvalidOnUpdate(String invalid , String expectedMessage) throws Exception {
            // Arrange
            Long clientId = 1L;
            String json = """
                    {
                        "clientFirstName": "%s",
                        "clientLastName": "Perez",
                        "clientEmail": "perez@gmail.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientFirstName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientId))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verfiy
            verifyNoInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidclientFirstName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientLasttName
        @ParameterizedTest
        @DisplayName("should return 400 when clientLastname is invalid on update")
        @MethodSource("provideInvalidclientFirstName")
        void shouldReturn400WhenClientLastNameIsInvalidOnUpdate(String invalid , String expectedMessage) throws Exception {
            // Arrange
            Long clientId = 1L;
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "%s",
                        "clientEmail": "perez@gmail.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientLastName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientId))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verfiy
            verifyNoInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidclientLastName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientEmail
        @ParameterizedTest
        @DisplayName("should return 400 when clientEmail is invalid on update")
        @MethodSource("provideInvalidclientEmail")
        void shouldReturn400WhenClientEmailIsInvalidOnUpdate(String invalid , String expectedMessage) throws Exception {
            // Arrange
            Long clientId = 1L;
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Peres",
                        "clientEmail": "%s",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientEmail")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientId))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verfiy
            verifyNoInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidclientEmail() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_EMAIL),
                    Arguments.of("@gmail.com",ERROR_EMAIL)
            );
        }

        // @Valid clientCardId
        @ParameterizedTest
        @DisplayName("should return 400 when clientCardId is invalid on update")
        @MethodSource("provideInvalidclientCardId")
        void shouldReturn400WhenClientCardIdIsInvalidOnUpdate(String invalid , String expectedMessage) throws Exception {
            // Arrange
            Long clientId = 1L;
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Peres",
                        "clientEmail": "juan@gmail.com",
                        "clientCardId": "%s",
                        "clientPhone": "987654322",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientCardId")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientId))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verfiy
            verifyNoInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidclientCardId() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_CARD),
                    Arguments.of("' OR '1'=' 1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientPhone
        @ParameterizedTest
        @DisplayName("should return 400 when clientPhone is invalid on update")
        @MethodSource("provideInvalidclientPhone")
        void shouldReturn400WhenclientPhoneIsInvalidOnUpdate(String invalid , String expectedMessage) throws Exception {
            // Arrange
            Long clientId = 1L;
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Peres",
                        "clientEmail": "juan@gmail.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "%s",
                        "clientAddress": "Av. Siempre Viva 123, Lima"
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientPhone")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientId))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verfiy
            verifyNoInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidclientPhone() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_PHONE),
                    Arguments.of("' OR '1'=' 1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid clientAddress
        @ParameterizedTest
        @DisplayName("should return 400 when clientAddress is invalid on update")
        @MethodSource("provideInvalidclientAddress")
        void shouldReturn400WhenclientAddressIsInvalidOnUpdate(String invalid , String expectedMessage) throws Exception {
            // Arrange
            Long clientId = 1L;
            String json = """
                    {
                        "clientFirstName": "Juan",
                        "clientLastName": "Peres",
                        "clientEmail": "juan@gmail.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654321",
                        "clientAddress": "%s"
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("clientAddress")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessage)))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientId))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verfiy
            verifyNoInteractions(clientService);
        }

        // method
        private static Stream<Arguments> provideInvalidclientAddress() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_ADDRESS),
                    Arguments.of("' OR '1'=' 1",ERROR_INVALID_FORMAT)
            );
        }

        // Json bad Format
        @Test
        @DisplayName("should return 400 when JSON is malformed on update")
        void shouldReturnMalformedJsonErrorOnUpdate() throws Exception {
            // Arrange
            Long clientId = 1L;
            String json = """
                    {
                        "clientFirstName": yes,
                        "clientLastName": "Peres",
                        "clientEmail": "juan@gmail.com",
                        "clientCardId": "ID12345699",
                        "clientPhone": "987654321",
                        "clientAddress": "Lima-"
                    }
                    """;
            // Act & Assert
            mockMvc.perform(put(APICLIENT+"/" + clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value("JSON bad format."))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(clientService);
        }




    }

    @Nested
    @DisplayName("DELETE/clients/{id}")
    class DeleteClientTest {

        // success
        @Test
        @DisplayName("should delete client successfully")
        void shouldeleteClientSuccessfully() throws Exception {
            // Arrange
            Long clientId = 1L;
            doNothing().when(clientService).deleteById(clientId);
            // Act & Assert
            mockMvc.perform(delete(APICLIENT+"/" + clientId))
                    .andExpect(status().isNoContent());

            // Verify
            verify(clientService).deleteById(clientId);
        }

        // fail
        @Test
        @DisplayName("should return 404 when deleting non-existent client")
        void shouldReturnNotFoundWhenDeletingNonExistentClient() throws Exception {
            // Arrange
            Long clientIdNonExistent = 99L;
            doThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND)).when(clientService).deleteById(clientIdNonExistent);
            // Act & Assert
            mockMvc.perform(delete(APICLIENT+"/" + clientIdNonExistent))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.path").value(APICLIENT+"/"+clientIdNonExistent))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(clientService).deleteById(clientIdNonExistent);
        }

    }

}
