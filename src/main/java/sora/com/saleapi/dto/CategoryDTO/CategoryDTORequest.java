package sora.com.saleapi.dto.CategoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryDTORequest(
        @NotNull(message = "El nombre de la categoría es obligatorio")
        @NotEmpty(message = "El nombre de la categoría no debe estar vacio")
        @Size(max = 50)
        String categoryName,

        @NotNull
        @NotEmpty
        @Size(max = 250)
        String categoryDescription,

        @NotNull
        Boolean categoryEnabled
) {}
