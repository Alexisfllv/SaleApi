package sora.com.saleapi.serviceimplTest;


// static
import org.junit.jupiter.api.BeforeEach;
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

    private RoleDTORequest roleDTORequest1;
    private Role roleRequest1;

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

    // listado exitoso
    @Test
    void TestListado(){

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

    // test de listado paginado
    @Test
    void TestListUserPage(){
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> listadoPaginado = new PageImpl<>(List.of(user1,user2,user3));
        when(userRepo.findAll(pageable)).thenReturn(listadoPaginado);
        when(userMapper.toUserDTOResponse(user1)).thenReturn(userDTOResponse1);
        when(userMapper.toUserDTOResponse(user2)).thenReturn(userDTOResponse2);
        when(userMapper.toUserDTOResponse(user3)).thenReturn(userDTOResponse3);

        // Act
        Page<UserDTOResponse> result = userService.findAllPage(pageable);

        // Assert & Verify
        assertAll(
                () -> assertEquals(3,result.getContent().size()),
                () -> assertEquals(userDTOResponse1,result.getContent().get(0)),
                () -> assertEquals(userDTOResponse2,result.getContent().get(1)),
                () -> assertEquals(userDTOResponse3,result.getContent().get(2))
        );
        verify(userRepo,times(1)).findAll(pageable);
        verify(userMapper,times(1)).toUserDTOResponse(user1);
        verify(userMapper,times(1)).toUserDTOResponse(user2);
        verify(userMapper,times(1)).toUserDTOResponse(user3);
        verifyNoMoreInteractions(userRepo,userMapper);
    }

    // test paginado de listado vacio
    @Test
    void TestListEmpyPageable(){
        // Arrage
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> listadoPaginado = new PageImpl<>(List.of());
        when(userRepo.findAll(pageable)).thenReturn(listadoPaginado);
        // Act
        Page<UserDTOResponse> result = userService.findAllPage(pageable);
        // Assert & Verify
        assertAll(
                () -> assertTrue(result.isEmpty()),
                () -> assertEquals(0,result.getContent().size())
        );
        verify(userRepo,times(1)).findAll(pageable);
        verifyNoMoreInteractions(userRepo);
    }

    // busqueda por id user
    @Test
    void findbyIdSuccess(){
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







}
