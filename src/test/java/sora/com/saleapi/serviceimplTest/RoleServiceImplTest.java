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
import sora.com.saleapi.entity.Role;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.RoleMapper;
import sora.com.saleapi.repo.RoleRepo;
import sora.com.saleapi.service.Impl.RoleServiceImpl;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    // mock
    @Mock
    private RoleRepo roleRepo;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    // variables reutilizables
    private Role role1;
    private Role role2;
    private Role role3;

    private RoleDTOResponse roleDTOResponse1;
    private RoleDTOResponse roleDTOResponse2;
    private RoleDTOResponse roleDTOResponse3;

    private RoleDTORequest roleDTORequest1;
    private Role roleRequest1;


    // asignar valores
    @BeforeEach
    public void setUp() {

        // start
        role1 = new Role(1L,"ADMIN",true);
        role2 = new Role(2L,"BD",true);
        role3 = new Role(3L,"EMPLOYEE",true);

        roleDTOResponse1 = new RoleDTOResponse(1L,"ADMIN",true);
        roleDTOResponse2 = new RoleDTOResponse(2L,"BD",true);
        roleDTOResponse3 = new RoleDTOResponse(3L,"EMPLOYEE",true);

        roleDTORequest1 = new RoleDTORequest("ADMIN",true);
        roleRequest1 = new Role(null, "ADMIN",true);

    }

    // test de listado
    @Test
    void givenRoleExist_whenListAll_thenReturnsAllRoles() {
        // Arrange
        List<Role> roles = List.of(role1,role2,role3);
        when(roleRepo.findAll()).thenReturn(roles);
        when(roleMapper.toRoleDTOResponse(role1)).thenReturn(roleDTOResponse1);
        when(roleMapper.toRoleDTOResponse(role2)).thenReturn(roleDTOResponse2);
        when(roleMapper.toRoleDTOResponse(role3)).thenReturn(roleDTOResponse3);
        // Act
        List<RoleDTOResponse> result = roleService.findAll();

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3,result.size()),
                () -> assertEquals(roleDTOResponse1,result.get(0)),
                () -> assertEquals(roleDTOResponse2,result.get(1)),
                () -> assertEquals(roleDTOResponse3,result.get(2))
        );

        verify(roleRepo).findAll();
        verify(roleMapper).toRoleDTOResponse(role1);
        verify(roleMapper).toRoleDTOResponse(role2);
        verify(roleMapper).toRoleDTOResponse(role3);
        verifyNoMoreInteractions(roleRepo,roleMapper);
    }

    // test listado paginado
    @Test
    void givenRolesExist_whenListAllPageable_thenReturnsAllRoles() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> roles = new PageImpl<>(List.of(role1,role2));

        RoleDTOResponse roldto1 = roleDTOResponse1;
        RoleDTOResponse roldto2 = roleDTOResponse2;

        when(roleRepo.findAll(pageable)).thenReturn(roles);
        when(roleMapper.toRoleDTOResponse(role1)).thenReturn(roldto1);
        when(roleMapper.toRoleDTOResponse(role2)).thenReturn(roldto2);

        // Act
        Page<RoleDTOResponse> result = roleService.findAllPage(pageable);

        // Assert & Verify
        assertAll(
                () -> assertEquals(2,result.getContent().size()),
                () -> assertEquals("ADMIN", result.getContent().get(0).roleName())
        );
        verify(roleRepo).findAll(pageable);
        verify(roleMapper).toRoleDTOResponse(role1);
        verify(roleMapper).toRoleDTOResponse(role2);
        verifyNoMoreInteractions(roleRepo,roleMapper);
    }

    // test de listado retorno vacio
    @Test
    void givenListRolesEmpty_whenListAllPageable_thenReturnsEmptyListPage() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> roles = new PageImpl<>(List.of());
        when(roleRepo.findAll(pageable)).thenReturn(roles);

        // Act
        Page<RoleDTOResponse> result = roleService.findAllPage(pageable);
        assertAll(
                () -> assertEquals(0, result.getContent().size())
        );
        verify(roleRepo).findAll(pageable);
        verifyNoMoreInteractions(roleRepo,roleMapper);

    }

    // test de busqueda exitoso rol
    @Test
    void givenExistRoleId_whenFindById_thenReturnsRoleDTOResponse() {
        // Arrange
        Long roleId = 1L;
        when(roleRepo.findById(roleId)).thenReturn(Optional.of(role1));
        when(roleMapper.toRoleDTOResponse(role1)).thenReturn(roleDTOResponse1);

        // Act
        RoleDTOResponse result = roleService.findById(roleId);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(roleDTOResponse1,result),
                () -> assertEquals(roleId,result.roleId()),
                () -> assertEquals("ADMIN",result.roleName()),
                () -> assertEquals(true,result.roleEnabled())
        );
        verify(roleRepo).findById(roleId);
        verify(roleMapper).toRoleDTOResponse(role1);
        verifyNoMoreInteractions(roleRepo,roleMapper);
    }

    // test de busqueda rol no existente
    @Test
    void giveNonExistRoleId_whenFindById_thenThrowsResourceNotFoundException() {

        // Arrange
        Long roleIdNoExist = 99L;
        when(roleRepo.findById(roleIdNoExist)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.findById(roleIdNoExist));

        // Verify
        verify(roleRepo).findById(roleIdNoExist);
        verifyNoMoreInteractions(roleRepo,roleMapper);
    }

}
