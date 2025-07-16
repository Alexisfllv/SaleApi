package sora.com.saleapi.dto.UserDTO;

import jakarta.validation.constraints.*;

public record UserDTORequest(

        @NotBlank(message = "{field.required}")
        @Size(max = 50, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String userName,

        @NotBlank(message = "{field.required}")
        @Size(max = 100, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String password,

        @NotNull(message = "{field.required}")       // Boolean
        Boolean userEnabled,

        @NotNull(message = "{field.required}")
        @Positive(message = "{field.must.be.positive}")    // Boolean
        Long roleId
) {}