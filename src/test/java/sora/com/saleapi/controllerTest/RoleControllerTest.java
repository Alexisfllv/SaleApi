package sora.com.saleapi.controllerTest;
// static
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
    public static final String MESSAGE_CREATED = "Creado correctamente";
    public static final String MESSAGE_UPDATED = "Actualizado correctamente";
    public static final String MESSAGE_DELETED = "Eliminado correctamente";
    public static final String MESSAGE_NOT_FOUND = "Role not found";
    public static final String ERROR_REQUIRED = "This field is required";
    public static final String ERROR_INVALID_FORMAT = "Invalid format";
    public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";

    @Nested
    class GetRoleTests {

        // list
        @Test
        public void shouldGetListRole() throws Exception {
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
        public void shouldGetListPageRole() throws Exception {
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
        public void shouldGetListPageEmptyRole() throws Exception {
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
        public void shouldGetRole() throws Exception {
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
        public void shouldGetRoleNotFound() throws Exception {
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
    class PostRoleTests {

        // save
        @Test
        void shouldPostCategory() throws Exception {
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
        @MethodSource("provideInvalidRoleName")
        void shouldPostInvalidRoleName(String invalid,String expectedMessageFragment ) throws Exception {
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
        void shouldReturnValidNotNullRoleEnabled() throws Exception {
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
        void shouldReturnInvalidJson() throws Exception {
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
    class PutRoleTests {

        // succes
        @Test
        void shouldPutCategory() throws Exception {
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
        void shouldPutCategoryNotFound() throws Exception {
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
        @MethodSource("provideInvalidRoleName")
        void shouldValidationError_whenInvalidRoleName(String invalid,String expectedMessageFragment) throws Exception {
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
        void shouldNotNullValidationError() throws Exception {
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
        void shouldNotNullValidationErrorJson() throws Exception {
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
    class DeleteRoleTests {

        // succes
        @Test
         void shouldNotNullValidationError() throws Exception {
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
        void shouldNonExistIdRole() throws Exception {
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
