package sora.com.saleapi.dto.UserDTO;

public record UserDTOResponse(
        Long userId,
        String userName,
        Boolean userEnabled,
        String roleName
) {}
