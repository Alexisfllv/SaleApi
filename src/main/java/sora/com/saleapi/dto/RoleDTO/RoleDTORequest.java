package sora.com.saleapi.dto.RoleDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RoleDTORequest(
        @NotBlank(message = "{field.required}")
        @Size(max = 50, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String roleName,

        @NotNull(message = "{field.required}")
        Boolean roleEnabled
) {}
