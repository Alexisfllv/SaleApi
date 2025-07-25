package sora.com.saleapi.serviceimplTest;


// static
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.dto.UserDTO.UserDTORequest;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.entity.Role;
import sora.com.saleapi.entity.User;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.UserMapper;
import sora.com.saleapi.repo.RoleRepo;
import sora.com.saleapi.repo.UserRepo;
import sora.com.saleapi.service.Impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    // mock de user repo
    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    // variables reutilizables
    private User user1;
    private User user2;
    private User user3;

    private UserDTOResponse userDTOResponse1;
    private UserDTOResponse userDTOResponse2;
    private UserDTOResponse userDTOResponse3;

    private UserDTORequest userDTORequest1;
    private User userRequest1;

    // variables reutilizables
    private Role role1;
    private Role role2;
    private Role role3;

    private RoleDTOResponse roleDTOResponse1;
    private RoleDTOResponse roleDTOResponse2;
    private RoleDTOResponse roleDTOResponse3;

    @BeforeEach
    public void setUp() {
        // role
        role1 = new Role(1L,"ADMIN",true);
        role2 = new Role(2L,"BD",true);
        role3 = new Role(3L,"EMPLOYEE",true);

        roleDTOResponse1 = new RoleDTOResponse(1L,"ADMIN",true);
        roleDTOResponse2 = new RoleDTOResponse(2L,"BD",true);
        roleDTOResponse3 = new RoleDTOResponse(3L,"EMPLOYEE",true);

        // user
        user1 = new User(1L,"Alexis","1234",true,role1);
        user2 = new User(2L, "María", "abcd", false, role2);
        user3 = new User(3L, "Luis", "pass123", true, role3);

        userDTOResponse1 = new UserDTOResponse(1L,"Alexis",true,roleDTOResponse1);
        userDTOResponse2 = new UserDTOResponse(2L, "María", false, roleDTOResponse2);
        userDTOResponse3 = new UserDTOResponse(3L, "Luis", true, roleDTOResponse3);

        userDTORequest1 = new UserDTORequest("Alexis","1234",true,role1.getRoleId());
        userRequest1 = new User(null,"Alexis","1234",true,role1);

    }


    @Nested
    @DisplayName("findAll()")
    class FindAll {

        // listado exitoso
        @Test
        @DisplayName("Should return all users when findAll is called")
        void shouldReturnAllUsersWhenFindAll() {

            // Arrange
            List<User> userList = List.of(user1,user2,user3);
            when(userRepo.findAll()).thenReturn(userList);
            when(userMapper.toUserDTOResponse(user1)).thenReturn(userDTOResponse1);
            when(userMapper.toUserDTOResponse(user2)).thenReturn(userDTOResponse2);
            when(userMapper.toUserDTOResponse(user3)).thenReturn(userDTOResponse3);
            // Act
            List<UserDTOResponse> result = userService.findAll();

            // Assert & Verify
            assertAll(
                    () -> assertEquals(3,result.size()),
                    () -> assertEquals(userDTOResponse1,result.get(0)),
                    () -> assertEquals(userDTOResponse2,result.get(1)),
                    () -> assertEquals(userDTOResponse3,result.get(2))
            );
            verify(userRepo,times(1)).findAll();
            verify(userMapper,times(1)).toUserDTOResponse(user1);
            verify(userMapper,times(1)).toUserDTOResponse(user2);
            verify(userMapper,times(1)).toUserDTOResponse(user3);
            verifyNoMoreInteractions(userRepo,userMapper);
        }
    }

    @Nested
    @DisplayName("findAllPage(Pageable)")
    class FindAllPage {
        // Should return paged categories when findAllPage is called
        // shouldReturnPagedCategoriesWhenFindAllPage

        // Should return empty paged categories when findAllPage is called
        // shouldReturnPagedEmptyCategoriesWhenFindAllPage

    }

    @Nested
    @DisplayName("findById(Long id)")
    class FindById {

        // busqueda por id user
        @Test
        @DisplayName("Should return user when findById is called")
        void shouldReturnUserWhenFindById() {
            // Arrange
            Long idUser = 1L;
            when(userRepo.findById(idUser)).thenReturn(Optional.of(user1));
            when(userMapper.toUserDTOResponse(user1)).thenReturn(userDTOResponse1);
            // Act
            UserDTOResponse result =  userService.findById(idUser);

            // Assert & Verify
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(1L,result.userId()),
                    () -> assertEquals("Alexis",result.userName()),
                    () -> assertEquals(true,result.userEnabled()),
                    () -> assertEquals(1L,result.role().roleId()),
                    () -> assertEquals("ADMIN",result.role().roleName()),
                    () -> assertEquals(true,result.role().roleEnabled())
            );
            verify(userRepo,times(1)).findById(idUser);
            verify(userMapper,times(1)).toUserDTOResponse(user1);
            verifyNoMoreInteractions(userRepo,userMapper);
            // user1 = new User(1L,"Alexis","1234",true,role1); ADMIN true
        }
        // busqueda por id user no existente
        @Test
        @DisplayName("Should throw ResourceNotFoundexception when update is called with invalid UserId")
        void shouldThrowNotFoundWhenUpdateIsCalledWithInvalidUserId() {
            // Arrange
            Long idUser = 99L;
            when(userRepo.findById(idUser)).thenReturn(Optional.empty());
            // Act
            assertThrows(ResourceNotFoundException.class, () -> userService.findById(idUser));

            // Assert & Verify
            verify(userRepo,times(1)).findById(idUser);
            verifyNoMoreInteractions(userRepo);
        }
    }

    @Nested
    @DisplayName("save(dataDTORequest)")
    class Save {

        // guardar user
        @Test
        @DisplayName("Should return created user when save is called")
        void shouldReturnCreatedUserWhenSave() {
            // Arrange
            UserDTORequest userDTOReq = userDTORequest1;
            User userReq = userRequest1;
            Role role = role1;
            Long idRole = userDTOReq.roleId();
            when(userMapper.toUser(any(UserDTORequest.class))).thenReturn(user1);
            when(roleRepo.findById(idRole)).thenReturn(Optional.of(role));
            when(userRepo.save(any(User.class))).thenReturn(user1);
            when(userMapper.toUserDTOResponse(any(User.class))).thenReturn(userDTOResponse1);
            // Act
            UserDTOResponse result = userService.save(userDTOReq);
            // Assert & Verify
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(1L,result.userId()),
                    () -> assertEquals("Alexis",result.userName()),
                    () -> assertEquals(true,result.userEnabled()),
                    () -> assertEquals(1L,result.role().roleId()),
                    () -> assertEquals("ADMIN",result.role().roleName()),
                    () -> assertEquals(true,result.role().roleEnabled())
            );
            verify(userMapper,times(1)).toUser(any(UserDTORequest.class));
            verify(roleRepo,times(1)).findById(idRole);
            verify(userRepo, times(1)).save(any(User.class));
            verify(userMapper,times(1)).toUserDTOResponse(any(User.class));
            verifyNoMoreInteractions(roleRepo,userRepo,userMapper);
        }

        // save id role resource not found
        @Test
        @DisplayName("Should throw ResourceNotFoundException when save is called with invalid User.RoleId")
        void shouldThrowNotFoundWhenSaveIsCalledWithInvalidUser() {
            // Arrange
            Long idRoleNonExist = 99L;
            // new user
            UserDTORequest userDTOReq = new UserDTORequest("Ferr","12345",true, idRoleNonExist);

            when(roleRepo.findById(idRoleNonExist)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,() -> userService.save(userDTOReq));

            // Assert & Verify
            verify(roleRepo,times(1)).findById(idRoleNonExist);
            verifyNoMoreInteractions(roleRepo);
        }


    }

    @Nested
    @DisplayName("update(Long id, dataDTORequest)")
    class Update {
        // update user
        @Test
        @DisplayName("Should return updated user when update is called")
        void shouldReturnUpdateUserWhenUpdate() {
            // Arrange
            Long idUser = 1L;
            // new user
            UserDTORequest userDTOReq = new UserDTORequest("Ferr","12345",true, role2.getRoleId());
            User userReq = new User(null,"Ferr","12345",true, role2);
            UserDTOResponse userDTORes = new UserDTOResponse(idUser,"Ferr",true, roleDTOResponse2);

            when(userRepo.findById(idUser)).thenReturn(Optional.of(user1));
            when(roleRepo.findById(role2.getRoleId())).thenReturn(Optional.of(role2));
            when(userRepo.save(any(User.class))).thenReturn(userReq);
            when(userMapper.toUserDTOResponse(any(User.class))).thenReturn(userDTORes);
            // Act
            UserDTOResponse result = userService.update(idUser,userDTOReq);
            // Assert & Verify
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(1L,result.userId()),
                    () -> assertEquals("Ferr",result.userName()),
                    () -> assertEquals(true,result.userEnabled()),
                    () -> assertEquals(2L,result.role().roleId()),
                    () -> assertEquals("BD",result.role().roleName()),
                    () -> assertEquals(true,result.role().roleEnabled())
            );
            verify(userRepo,times(1)).findById(idUser);
        }
        // update user id user no encontrado
        @Test
        @DisplayName("Should throw ResourcenotFoundException when update is called with invalid UserId")
        void shouldThrowNotFoundWhenUpdateIsCalledWithInvalidUserId() {

            // Arrange
            Long idUserNonExist = 99L;
            UserDTORequest userExist = userDTORequest1;
            when(userRepo.findById(idUserNonExist)).thenReturn(Optional.empty());
            // Act & Assert
            assertThrows(ResourceNotFoundException.class,() -> userService.update(idUserNonExist,userExist));
            // Verify
            verify(userRepo,times(1)).findById(idUserNonExist);
            verifyNoMoreInteractions(userRepo);
        }

        // update id role rousrece not found
        @Test
        @DisplayName("Should throw ResourceNotFoundException when update is called with invalid User.RoleId")
        void shouldThrowNotFoundWhenUpdateIsCalledWithInvalidRoleId() {
            // Arrange
            Long idUser = 1L;
            Long idRoleNonExist = 99L;
            // new user
            UserDTORequest userDTOReq = new UserDTORequest("Ferr","12345",true, idRoleNonExist);

            when(userRepo.findById(idUser)).thenReturn(Optional.of(user1));
            when(roleRepo.findById(idRoleNonExist)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,() -> userService.update(idUser,userDTOReq));

            // Assert & Verify

            verify(userRepo,times(1)).findById(idUser);
            verify(roleRepo,times(1)).findById(idRoleNonExist);
            verifyNoMoreInteractions(userRepo,roleRepo);
        }

    }

    @Nested
    @DisplayName("deleteById(Long id)")
    class DeleteById {
        // delete user correcto
        @Test
        @DisplayName("Should delete user when deleteById is called")
        void shouldDeleteUserWhenDeleteById() {
            // Arrange
            Long idUser = 1L;
            User userExist = user1;
            when(userRepo.findById(idUser)).thenReturn(Optional.of(userExist));

            // Act
            userService.deleteById(idUser);
            // Asset & Verify
            verify(userRepo,times(1)).findById(idUser);
            verify(userRepo,times(1)).delete(userExist);
            verifyNoMoreInteractions(userRepo);
        }
        // delete fallido
        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleteById is called with invalid UserId")
        void shouldThrowNotFoundWhenDeleteByIdIsCalledWithInvalidUserId() {
            // Arrange
            Long idUserNonExist = 99L;
            when(userRepo.findById(idUserNonExist)).thenReturn(Optional.empty());
            // Act & Assert
            assertThrows(ResourceNotFoundException.class,() -> userService.deleteById(idUserNonExist));
            // Verify
            verify(userRepo,times(1)).findById(idUserNonExist);
            verifyNoMoreInteractions(userRepo);
        }
    }










}
