package sora.com.saleapi.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.entity.Role;
import sora.com.saleapi.mapper.RoleMapper;
import sora.com.saleapi.repo.RoleRepo;
import sora.com.saleapi.service.RoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    // ioc
    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleDTOResponse> findAll() {
        List<Role> lista = roleRepo.findAll();
        return lista.stream()
                .map(role -> roleMapper.toRoleDTOResponse(role))
                .toList();
    }

    @Override
    public RoleDTOResponse findById(Long id) {
        Role role = roleRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Role not found"));
        return roleMapper.toRoleDTOResponse(role);
    }

    @Override
    public RoleDTOResponse save(RoleDTORequest roleDTORequest) {
        Role role = roleMapper.toRole(roleDTORequest);
        roleRepo.save(role);
        return roleMapper.toRoleDTOResponse(role);
    }

    @Override
    public RoleDTOResponse update(Long id, RoleDTORequest roleDTORequest) {
        Role role = roleRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Role not found"));

        role.setRoleName(roleDTORequest.roleName());
        role.setRoleEnabled(roleDTORequest.roleEnabled());
        roleRepo.save(role);
        return roleMapper.toRoleDTOResponse(role);
    }

    @Override
    public void deleteById(Long id) {

        Role role = roleRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Role not found"));
        roleRepo.delete(role);
    }
}
