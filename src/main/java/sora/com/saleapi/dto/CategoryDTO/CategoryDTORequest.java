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

        @NotNull(message = "El campo de descripcion no debe ser null")
        @NotEmpty (message = "El campo de descripcion no debe estar vacio")
        @Size(max = 250)
        String categoryDescription,

        @NotNull(message = "El campo enabled no debe ser null")
        Boolean categoryEnabled
) {}
