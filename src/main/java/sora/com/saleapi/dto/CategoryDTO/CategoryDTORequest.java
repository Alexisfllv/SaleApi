package sora.com.saleapi.dto.CategoryDTO;

import jakarta.validation.constraints.*;

public record CategoryDTORequest(

        @NotBlank(message = "{field.required}")      // solo para String
        @Size(max = 50, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String categoryName,

        @NotBlank(message = "{field.required}")      // también String
        @Size(max = 250, min = 2, message = "{field.max.length}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String categoryDescription,

        @NotNull(message = "{field.required}")       // Boolean
        Boolean categoryEnabled
) {}
