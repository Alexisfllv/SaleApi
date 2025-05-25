package sora.com.saleapi.dto.RoleDTO;

public record RoleDTOResponse(
        Long roleId,
        String roleName,
        Boolean roleEnabled
) {}
