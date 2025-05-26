package sora.com.saleapi.dto.CategoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryDTORequest(
        @NotNull
        @Size(max = 50)
        String categoryName,

        @NotNull
        @Size(max = 250)
        String categoryDescription,

        @NotNull
        Boolean categoryEnabled
) {}
