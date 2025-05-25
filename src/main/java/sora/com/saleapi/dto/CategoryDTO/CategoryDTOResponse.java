package sora.com.saleapi.dto.CategoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryDTOResponse(
        Long categoryId,
        String categoryName,
        String categoryDescription,
        Boolean categoryEnabled
) { }
