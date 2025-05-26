package sora.com.saleapi.service;

import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;

import java.util.List;

public interface RoleService {
    // metodos
    List<RoleDTOResponse> findAll();
    RoleDTOResponse findById(Long id);
    RoleDTOResponse save(RoleDTORequest roleDTORequest);
    RoleDTOResponse update(Long id, RoleDTORequest roleDTORequest);
    void deleteById(Long id);
}
