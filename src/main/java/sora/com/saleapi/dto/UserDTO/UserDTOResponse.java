package sora.com.saleapi.dto.UserDTO;

import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;

public record UserDTOResponse(
        Long userId,
        String userName,
        Boolean userEnabled,
        RoleDTOResponse role
) {}
