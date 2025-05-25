package sora.com.saleapi.dto.CategoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryDTORequest(
        @NotBlank
        @Size(max = 50)
        String categoryName,

        @NotBlank
        @Size(max = 250)
        String categoryDescription,

        @NotNull
        Boolean categoryEnabled
) {}
