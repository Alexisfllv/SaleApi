package sora.com.saleapi.controllerTest;
// static
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sora.com.saleapi.controller.RoleController;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.service.RoleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(RoleController.class)
@DisplayName("RoleController Test Suite")
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    // var
    private RoleDTORequest roleDTORequest;
    private RoleDTOResponse roleDTOResponse;
    private RoleDTOResponse roleDTOResponse2;
    private List<RoleDTOResponse> roleDTOResponseList;

    private String APIROLE = "/api/v1/roles";

    @BeforeEach
    public void setup() {
        roleDTORequest = new RoleDTORequest("ADMIN",true);
        roleDTOResponse = new RoleDTOResponse(1L,"ADMIN",true);
        roleDTOResponse2 = new RoleDTOResponse(2L,"DEV",false);
        roleDTOResponseList = List.of(roleDTOResponse, roleDTOResponse2);
    }

    // CONTANTS

    public static final String MESSAGE_NOT_FOUND = "Role not found";
    public static final String ERROR_REQUIRED = "This field is required";
    public static final String ERROR_INVALID_FORMAT = "Invalid format";
    public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";

    @Nested
    @DisplayName("GET /roles")
    class GetRoleTests {

        // list
        @Test
        @DisplayName("should return full list of roles")
        public void shouldReturnListOfRoles() throws Exception {
            // Arrange
            when(roleService.findAll()).thenReturn(roleDTOResponseList);
            // Act & Assert
            mockMvc.perform(get(APIROLE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].roleId").value(1L))
                    .andExpect(jsonPath("$[0].roleName").value("ADMIN"))
                    .andExpect(jsonPath("$[1].roleId").value(2L))
                    .andExpect(jsonPath("$[1].roleName").value("DEV"));
            // Verify
            verify(roleService, times(1)).findAll();
        }

        // list Page
        @Test
        @DisplayName("should return paginated list of roles")
        public void shouldReturnPagedListOfRoles() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("roleId").ascending());
            Page<RoleDTOResponse> pageRole = new PageImpl<>(roleDTOResponseList, pageable, roleDTOResponseList.size());

            when(roleService.findAllPage(any(Pageable.class))).thenReturn(pageRole);
            // Act & Assert
            mockMvc.perform(get(APIROLE + "/page")
                            .param("page", "0")
                            .param("size", "5")
                            .param("sort", "roleId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].roleId").value(1L))
                    .andExpect(jsonPath("$.content[0].roleName").value("ADMIN"))
                    .andExpect(jsonPath("$.content[1].roleId").value(2L))
                    .andExpect(jsonPath("$.content[1].roleName").value("DEV"));
            // Verify
            verify(roleService, times(1)).findAllPage(any(Pageable.class));
        }

        // list page empty
        @Test
        @DisplayName("should return empty paginated list of roles")
        public void shouldReturnEmptyPagedListOfRoles() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("roleId").ascending());
            Page<RoleDTOResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(roleService.findAllPage(any(Pageable.class))).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(APIROLE + "/page")
                            .param("page", "0")
                            .param("size", "5")
                            .param("sort", "roleId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(0)));

            // Verify
            verify(roleService, times(1)).findAllPage(any(Pageable.class));
        }

        // findbyId
        @Test
        @DisplayName("should return role by ID")
        public void shouldReturnRoleById() throws Exception {
            // Arrange
            Long roleId = 1L;
            when(roleService.findById(roleId)).thenReturn(roleDTOResponse);

            // Act & Assert
            mockMvc.perform(get(APIROLE + "/" + roleId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.roleId").value(roleId))
                    .andExpect(jsonPath("$.roleName").value("ADMIN"))
                    .andExpect(jsonPath("$.roleEnabled").value(true));

            // Verify
            verify(roleService, times(1)).findById(roleId);
        }

        // findByIdFail
        @Test
        @DisplayName("should return 404 when role ID does not exist")
        public void shouldReturnNotFoundWhenRoleDoesNotExist() throws Exception {
            // Act
            Long roleIdNonExistent = 1L;
            when(roleService.findById(roleIdNonExistent)).thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));

            // Arrange & Assert
            mockMvc.perform(get(APIROLE + "/" + roleIdNonExistent))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.path").value(APIROLE + "/" + roleIdNonExistent))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verify(roleService, times(1)).findById(roleIdNonExistent);
        }

    }

    @Nested
    @DisplayName("POST /roles")
    class PostRoleTests {

        // save
        @Test
        @DisplayName("should create role successfully")
        void shouldCreateRoleSuccessfully() throws Exception {
            // Arrange
            when(roleService.save(any(RoleDTORequest.class))).thenReturn(roleDTOResponse);

            // Act & Assert
            mockMvc.perform(post(APIROLE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(roleDTORequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.roleId").value(1L))
                    .andExpect(jsonPath("$.roleName").value("ADMIN"))
                    .andExpect(jsonPath("$.roleEnabled").value(true));
            // Verify
            verify(roleService, times(1)).save(any(RoleDTORequest.class));
        }

        // @Valid roleName
        @ParameterizedTest
        @DisplayName("should return 400 when roleName is invalid")
        @MethodSource("provideInvalidRoleName")
        void shouldReturnValidationErrorForInvalidRoleName(String invalid,String expectedMessageFragment ) throws Exception {
            // Arrange
            String json = """
                    {
                        "roleName": "%s",
                        "roleEnabled": true
                    }
                   """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIROLE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("roleName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value(APIROLE))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoMoreInteractions(roleService);
        }

        private static Stream<Arguments> provideInvalidRoleName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("👻",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid not null roleEnabled
        @Test
        @DisplayName("should return 400 when roleEnabled is null")
        void shouldReturnValidationErrorForNullRoleEnabled() throws Exception {
            // Arrange
            String json = """
                    {
                        "roleName": "ADMIN",
                        "roleEnabled": null
                    }
                   """;
            // Act & Assert
            mockMvc.perform(post(APIROLE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("roleEnabled")))
                    .andExpect(jsonPath("$.message").value(containsString(ERROR_REQUIRED)))
                    .andExpect(jsonPath("$.path").value(APIROLE))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(roleService);
        }

        // @Valid not null roleEnabled
        @Test
        @DisplayName("should return 400 when JSON is malformed")
        void shouldReturnMalformedJsonError() throws Exception {
            // Arrange
            String json = """
                    {
                        "roleName": "ADMIN",
                        "roleEnabled": yes
                    }
                   """;
            // Act & Assert
            mockMvc.perform(post(APIROLE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value("JSON bad format."))
                    .andExpect(jsonPath("$.path").value(APIROLE))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(roleService);
        }

    }

    @Nested
    @DisplayName("PUT /roles/{id}")
    class PutRoleTests {

        // succes
        @Test
        @DisplayName("should update role successfully")
        void shouldUpdateRoleSuccessfully() throws Exception {
            // Arrange
            Long roleId = 1L;
            RoleDTORequest updateRequest = new RoleDTORequest("BD",true);
            RoleDTOResponse updateResponse = new RoleDTOResponse(roleId,"BD",true);

            when(roleService.update(eq(roleId), any(RoleDTORequest.class))).thenReturn(updateResponse);
            // Act & Assert
            mockMvc.perform(put(APIROLE+"/"+roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.roleId").value(roleId))
                    .andExpect(jsonPath("$.roleName").value("BD"))
                    .andExpect(jsonPath("$.roleEnabled").value(true));
            // Verify
            verify(roleService, times(1)).update(eq(roleId), any(RoleDTORequest.class));
        }

        // fail
        @Test
        @DisplayName("should return 404 when updating non-existent role")
        void shouldReturnNotFoundWhenUpdatingNonExistentRole() throws Exception {
            // Arrange
            Long roleId = 99L;
            RoleDTORequest updateRequest = new RoleDTORequest("BD",true);

            when(roleService.update(eq(roleId), any(RoleDTORequest.class)))
                    .thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));

            // Act & Assert
            mockMvc.perform(put(APIROLE+"/"+roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(roleService, times(1)).update(eq(roleId), any(RoleDTORequest.class));
            verifyNoMoreInteractions(roleService);
        }

        // @Valid roleName
        @ParameterizedTest
        @DisplayName("should return 400 when roleName is invalid on update")
        @MethodSource("provideInvalidRoleName")
        void shouldReturnValidationErrorForInvalidRoleNameOnUpdate(String invalid,String expectedMessageFragment) throws Exception {
            // Arrange
            Long roleId = 1L;
            String json = """
                    {
                        "roleName": "%s",
                        "roleEnabled": true
                    }
                   """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APIROLE+"/"+roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("roleName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(roleService);
        }

        private static Stream<Arguments> provideInvalidRoleName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("👻",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid not null roleEnabled

        @Test
        @DisplayName("should return 400 when roleEnabled is null on update")
        void shouldReturnValidationErrorForNullRoleEnabledOnUpdate() throws Exception {
            // Arrange
            Long roleId = 1L;
            String json = """
                    {
                        "roleName": "name",
                        "roleEnabled": null
                    }
                   """;
            // Act & Assert
            mockMvc.perform(put(APIROLE+"/"+roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("roleEnabled")))
                    .andExpect(jsonPath("$.message").value(containsString(ERROR_REQUIRED)))
                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(roleService);
        }

        //JSON BAD FORMAT
        @Test
        @DisplayName("should return 400 when JSON is malformed on update")
        void shouldReturnMalformedJsonErrorOnUpdate() throws Exception {
            // Arrange
            Long roleId = 1L;
            String json = """
                    {
                        "roleName": "name",
                        "roleEnabled": yes
                    }
                   """;
            // Act & Assert
            mockMvc.perform(put(APIROLE+"/"+roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value("JSON bad format."))
                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(roleService);
        }

    }

    @Nested
    @DisplayName("DELETE /roles/{id}")
    class DeleteRoleTests {

        // succes
        @Test
        @DisplayName("should delete role successfully")
        void shouldDeleteRoleSuccessfully() throws Exception {
            // Arrange
            Long roleId = 1L;
            doNothing().when(roleService).deleteById(roleId);

            // Act & Assert
            mockMvc.perform(delete(APIROLE+"/"+roleId))
                    .andExpect(status().isNoContent());
            // Verify
            verify(roleService).deleteById(roleId);
        }

        // fail non exist
        @Test
        @DisplayName("should return 404 when deleting non-existent role")
        void shouldReturnNotFoundWhenDeletingNonExistentRole() throws Exception {
            // Arrange
            Long roleIdNonExist = 99L;
            doThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND))
                    .when(roleService).deleteById(roleIdNonExist);
            // Act & Assert
            mockMvc.perform(delete(APIROLE+"/"+roleIdNonExist))
                            .andExpect(status().isNotFound())
                            .andExpect(jsonPath("$.status").value(404))
                            .andExpect(jsonPath("$.error").value("Not Found"))
                            .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                            .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                            .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleIdNonExist))
                            .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(roleService).deleteById(roleIdNonExist);
        }
    }

}
