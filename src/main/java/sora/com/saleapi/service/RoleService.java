package sora.com.saleapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;

import java.util.List;

public interface RoleService {
    // metodos
    List<RoleDTOResponse> findAll();
    Page<RoleDTOResponse> findAllPage(Pageable pageable);
    RoleDTOResponse findById(Long id);
    RoleDTOResponse save(RoleDTORequest roleDTORequest);
    RoleDTOResponse update(Long id, RoleDTORequest roleDTORequest);
    void deleteById(Long id);
}
