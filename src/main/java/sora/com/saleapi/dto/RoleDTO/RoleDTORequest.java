package sora.com.saleapi.dto.RoleDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoleDTORequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50)
        String roleName,

        @NotNull(message = "El estado del rol es obligatorio")
        Boolean roleEnabled
) {}
