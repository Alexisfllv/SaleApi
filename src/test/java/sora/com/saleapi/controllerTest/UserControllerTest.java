package sora.com.saleapi.controllerTest;
// static

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apiguardian.api.API;
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
import sora.com.saleapi.controller.RoleController;
import sora.com.saleapi.controller.UserController;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.dto.UserDTO.UserDTORequest;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.service.RoleService;
import sora.com.saleapi.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Test Suite")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // var
    private RoleDTORequest roleDTORequest;
    private RoleDTOResponse roleDTOResponse;
    private RoleDTOResponse roleDTOResponse2;

    private UserDTORequest userDTORequest;
    private UserDTOResponse userDTOResponse;
    private UserDTOResponse userDTOResponse2;

    private List<UserDTOResponse> userDTOResponseList;

    private String APIUSER = "/api/v1/users";

    @BeforeEach
    public void setup() {
        roleDTORequest = new RoleDTORequest("ADMIN",true);
        roleDTOResponse = new RoleDTOResponse(1L,"ADMIN",true);
        roleDTOResponse2 = new RoleDTOResponse(2L,"DEV",false);

        userDTORequest = new UserDTORequest("Alexis","passwordTest",true,1L);
        userDTOResponse = new UserDTOResponse(1L,"Alexis",true,roleDTOResponse);
        userDTOResponse2 = new UserDTOResponse(2L,"Ferrari",true,roleDTOResponse2);
        userDTOResponseList = List.of(userDTOResponse,userDTOResponse2);
    }

    // CONTANTS

    public static final String MESSAGE_NOT_FOUND = "User not found";
    public static final String ERROR_REQUIRED = "This field is required";
    public static final String ERROR_INVALID_FORMAT = "Invalid format";
    public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";
    public static final String ERROR_SIZE_PASSWORD = "The number of items must be between 2 and 100";
    public static final String ERROR_POSITIVE = "The value must be positive";

    @Nested
    @DisplayName("GET /users")
    class GetRoleTests {

        // list
        @Test
        @DisplayName("should return full list of users")
        public void shouldReturnListOfUsers() throws Exception {
            // Arrange
            when(userService.findAll()).thenReturn(userDTOResponseList);
            // Act & Assert
            mockMvc.perform(get(APIUSER))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].userId").value(1L))
                    .andExpect(jsonPath("$[0].userName").value("Alexis"))
                    .andExpect(jsonPath("$[1].userId").value(2L))
                    .andExpect(jsonPath("$[1].userName").value("Ferrari"));
            // Verify
            verify(userService, times(1)).findAll();
        }

        // list Page
        @Test
        @DisplayName("should return paginated list of users")
        public void shouldReturnPagedListOfUsers() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("userId").ascending());
            Page<UserDTOResponse> pageUser = new PageImpl<>(userDTOResponseList, pageable, userDTOResponseList.size());

            when(userService.findAllPage(any(Pageable.class))).thenReturn(pageUser);
            // Act & Assert
            mockMvc.perform(get(APIUSER + "/page")
                            .param("page", "0")
                            .param("size", "5")
                            .param("sort", "userId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].userId").value(1L))
                    .andExpect(jsonPath("$.content[0].userName").value("Alexis"))
                    .andExpect(jsonPath("$.content[1].userId").value(2L))
                    .andExpect(jsonPath("$.content[1].userName").value("Ferrari"));
            // Verify
            verify(userService, times(1)).findAllPage(any(Pageable.class));
        }

        // list page empty
        @Test
        @DisplayName("should return empty paginated list of users")
        public void shouldReturnEmptyPagedListOfUsers() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("userId").ascending());
            Page<UserDTOResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(userService.findAllPage(any(Pageable.class))).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(APIUSER + "/page")
                            .param("page", "0")
                            .param("size", "5")
                            .param("sort", "userId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(0)));

            // Verify
            verify(userService, times(1)).findAllPage(any(Pageable.class));
        }

        // findbyId
        @Test
        @DisplayName("should return user by ID")
        public void shouldReturnUserById() throws Exception {
            // Arrange
            Long userId = 1L;
            when(userService.findById(userId)).thenReturn(userDTOResponse);

            // Act & Assert
            mockMvc.perform(get(APIUSER + "/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.userName").value("Alexis"))
                    .andExpect(jsonPath("$.userEnabled").value(true))
                    .andExpect(jsonPath("$.role.roleId").value(1L))
                    .andExpect(jsonPath("$.role.roleName").value("ADMIN"))
                    .andExpect(jsonPath("$.role.roleEnabled").value(true));

            // Verify
            verify(userService, times(1)).findById(userId);
        }

        // findByIdFail
        @Test
        @DisplayName("should return 404 when user ID does not exist")
        public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
            // Act
            Long userIdNonExist = 1L;
            when(userService.findById(userIdNonExist)).thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));

            // Arrange & Assert
            mockMvc.perform(get(APIUSER + "/" + userIdNonExist))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.path").value(APIUSER + "/" + userIdNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verify(userService, times(1)).findById(userIdNonExist);
        }

    }

    @Nested
    @DisplayName("POST /users")
    class PostRoleTests {

        // save
        @Test
        @DisplayName("should create user successfully")
        void shouldCreateUserSuccessfully() throws Exception {
            // Arrange
            when(userService.save(any(UserDTORequest.class))).thenReturn(userDTOResponse);

            // Act & Assert
            mockMvc.perform(post(APIUSER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDTORequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId").value(1L))
                    .andExpect(jsonPath("$.userName").value("Alexis"))
                    .andExpect(jsonPath("$.userEnabled").value(true))
                    .andExpect(jsonPath("$.role.roleId").value(1L))
                    .andExpect(jsonPath("$.role.roleName").value("ADMIN"))
                    .andExpect(jsonPath("$.role.roleEnabled").value(true));
            // Verify
            verify(userService, times(1)).save(any(UserDTORequest.class));
        }

        // @Valid userName
        @ParameterizedTest
        @DisplayName("should return 400 when userName is invalid")
        @MethodSource("provideInvalidUserName")
        void shouldReturnValidationErrorForInvalidUserName(String invalid,String expectedMessageFragment ) throws Exception {
            // Arrange
            String json = """
                    {
                         "userName": "%s",
                         "password": "123",
                         "userEnabled": true,
                         "roleId": 1
                     }
                   """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIUSER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("userName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value(APIUSER))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoInteractions(userService);
        }

        private static Stream<Arguments> provideInvalidUserName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("👻",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid password
        @ParameterizedTest
        @DisplayName("should return 400 when password is invalid")
        @MethodSource("provideInvalidPassword")
        void shouldReturnValidationErrorForInvalidPassword(String invalid,String expectedMessageFragment ) throws Exception {
            // Arrange
            String json = """
                    {
                         "userName": "Alexis",
                         "password": "%s",
                         "userEnabled": true,
                         "roleId": 1
                     }
                   """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIUSER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("password")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value(APIUSER))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoInteractions(userService);
        }

        private static Stream<Arguments> provideInvalidPassword() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_PASSWORD),
                    Arguments.of("👻",ERROR_INVALID_FORMAT)
            );
        }
        // @Valid not null userEnabled
        @Test
        @DisplayName("should return 400 when userEnabled is null")
        void shouldReturnMalformedJsonError() throws Exception {
            // Arrange
            String json = """
                    {
                         "userName": "Alexis",
                         "password": "123",
                         "userEnabled": null,
                         "roleId": 1
                     }
                   """;
            // Act & Assert
            mockMvc.perform(post(APIUSER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("userEnabled")))
                    .andExpect(jsonPath("$.message").value(containsString(ERROR_REQUIRED)))
                    .andExpect(jsonPath("$.path").value(APIUSER))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(userService);
        }

        // @Valid roleId
        @ParameterizedTest
        @DisplayName("should return 400 when UserRoleId is invalid")
        @MethodSource("provideInvalidUserRoleId")
        void shouldReturnValidationErrorForInvalidUserRoleId(String invalid,String expectedMessageFragment ) throws Exception {
            // Arrange
            String json = """
                    {
                         "userName": "Alexis",
                         "password": "123",
                         "userEnabled": true,
                         "roleId": %s
                     }
                   """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIUSER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("roleId")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value(APIUSER))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoInteractions(userService);
        }

        private static Stream<Arguments> provideInvalidUserRoleId() {
            return Stream.of(
                    Arguments.of("null",ERROR_REQUIRED),
                    Arguments.of("-1",ERROR_POSITIVE)
            );
        }

    }

//    @Nested
//    @DisplayName("PUT /roles/{id}")
//    class PutRoleTests {
//
//        // succes
//        @Test
//        @DisplayName("should update role successfully")
//        void shouldUpdateRoleSuccessfully() throws Exception {
//            // Arrange
//            Long roleId = 1L;
//            RoleDTORequest updateRequest = new RoleDTORequest("BD",true);
//            RoleDTOResponse updateResponse = new RoleDTOResponse(roleId,"BD",true);
//
//            when(roleService.update(eq(roleId), any(RoleDTORequest.class))).thenReturn(updateResponse);
//            // Act & Assert
//            mockMvc.perform(put(APIROLE+"/"+roleId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(updateRequest)))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$.roleId").value(roleId))
//                    .andExpect(jsonPath("$.roleName").value("BD"))
//                    .andExpect(jsonPath("$.roleEnabled").value(true));
//            // Verify
//            verify(roleService, times(1)).update(eq(roleId), any(RoleDTORequest.class));
//        }
//
//        // fail
//        @Test
//        @DisplayName("should return 404 when updating non-existent role")
//        void shouldReturnNotFoundWhenUpdatingNonExistentRole() throws Exception {
//            // Arrange
//            Long roleId = 99L;
//            RoleDTORequest updateRequest = new RoleDTORequest("BD",true);
//
//            when(roleService.update(eq(roleId), any(RoleDTORequest.class)))
//                    .thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));
//
//            // Act & Assert
//            mockMvc.perform(put(APIROLE+"/"+roleId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(updateRequest)))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.status").value(404))
//                    .andExpect(jsonPath("$.error").value("Not Found"))
//                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
//                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
//                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
//                    .andExpect(jsonPath("$.timestamp").exists());
//            // Verify
//            verify(roleService, times(1)).update(eq(roleId), any(RoleDTORequest.class));
//            verifyNoMoreInteractions(roleService);
//        }
//
//        // @Valid roleName
//        @ParameterizedTest
//        @DisplayName("should return 400 when roleName is invalid on update")
//        @MethodSource("provideInvalidRoleName")
//        void shouldReturnValidationErrorForInvalidRoleNameOnUpdate(String invalid,String expectedMessageFragment) throws Exception {
//            // Arrange
//            Long roleId = 1L;
//            String json = """
//                    {
//                        "roleName": "%s",
//                        "roleEnabled": true
//                    }
//                   """.formatted(invalid);
//            // Act & Assert
//            mockMvc.perform(put(APIROLE+"/"+roleId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(json))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.error").value("Bad Request"))
//                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
//                    .andExpect(jsonPath("$.message").value(containsString("roleName")))
//                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
//                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
//                    .andExpect(jsonPath("$.timestamp").exists());
//            // Verify
//            verifyNoInteractions(roleService);
//        }
//
//        private static Stream<Arguments> provideInvalidRoleName() {
//            return Stream.of(
//                    Arguments.of("",ERROR_REQUIRED),
//                    Arguments.of("A",ERROR_SIZE_NAME),
//                    Arguments.of("👻",ERROR_INVALID_FORMAT)
//            );
//        }
//
//        // @Valid not null roleEnabled
//
//        @Test
//        @DisplayName("should return 400 when roleEnabled is null on update")
//        void shouldReturnValidationErrorForNullRoleEnabledOnUpdate() throws Exception {
//            // Arrange
//            Long roleId = 1L;
//            String json = """
//                    {
//                        "roleName": "name",
//                        "roleEnabled": null
//                    }
//                   """;
//            // Act & Assert
//            mockMvc.perform(put(APIROLE+"/"+roleId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(json))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.error").value("Bad Request"))
//                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
//                    .andExpect(jsonPath("$.message").value(containsString("roleEnabled")))
//                    .andExpect(jsonPath("$.message").value(containsString(ERROR_REQUIRED)))
//                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
//                    .andExpect(jsonPath("$.timestamp").exists());
//            // Verify
//            verifyNoInteractions(roleService);
//        }
//
//        //JSON BAD FORMAT
//        @Test
//        @DisplayName("should return 400 when JSON is malformed on update")
//        void shouldReturnMalformedJsonErrorOnUpdate() throws Exception {
//            // Arrange
//            Long roleId = 1L;
//            String json = """
//                    {
//                        "roleName": "name",
//                        "roleEnabled": yes
//                    }
//                   """;
//            // Act & Assert
//            mockMvc.perform(put(APIROLE+"/"+roleId)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(json))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.error").value("Bad Request"))
//                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
//                    .andExpect(jsonPath("$.message").value("JSON bad format."))
//                    .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleId))
//                    .andExpect(jsonPath("$.timestamp").exists());
//            // Verify
//            verifyNoInteractions(roleService);
//        }
//
//    }
//
//    @Nested
//    @DisplayName("DELETE /roles/{id}")
//    class DeleteRoleTests {
//
//        // succes
//        @Test
//        @DisplayName("should delete role successfully")
//        void shouldDeleteRoleSuccessfully() throws Exception {
//            // Arrange
//            Long roleId = 1L;
//            doNothing().when(roleService).deleteById(roleId);
//
//            // Act & Assert
//            mockMvc.perform(delete(APIROLE+"/"+roleId))
//                    .andExpect(status().isNoContent());
//            // Verify
//            verify(roleService).deleteById(roleId);
//        }
//
//        // fail non exist
//        @Test
//        @DisplayName("should return 404 when deleting non-existent role")
//        void shouldReturnNotFoundWhenDeletingNonExistentRole() throws Exception {
//            // Arrange
//            Long roleIdNonExist = 99L;
//            doThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND))
//                    .when(roleService).deleteById(roleIdNonExist);
//            // Act & Assert
//            mockMvc.perform(delete(APIROLE+"/"+roleIdNonExist))
//                            .andExpect(status().isNotFound())
//                            .andExpect(jsonPath("$.status").value(404))
//                            .andExpect(jsonPath("$.error").value("Not Found"))
//                            .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
//                            .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
//                            .andExpect(jsonPath("$.path").value(APIROLE+"/"+roleIdNonExist))
//                            .andExpect(jsonPath("$.timestamp").exists());
//            // Verify
//            verify(roleService).deleteById(roleIdNonExist);
//        }
//    }

}
