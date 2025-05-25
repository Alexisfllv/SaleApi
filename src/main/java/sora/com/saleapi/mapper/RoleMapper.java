package sora.com.saleapi.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.entity.Role;

@Mapper (componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    // Request
    Role toRole(RoleDTORequest roleDTORequest);

    // Response
    RoleDTOResponse toRoleDTOResponse(Role role);


}
