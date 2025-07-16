package sora.com.saleapi.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDTORequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(max = 50)
        String userName,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(max = 100)
        String password,

        @NotNull(message = "El estado del usuario es obligatorio")
        Boolean userEnabled,

        @NotNull(message = "El ID del rol es obligatorio")
        Long roleId
) {}