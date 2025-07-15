package sora.com.saleapi.dto.ProductDTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductDTORequest(
        @NotBlank(message = "{field.required}")
        @Size(max = 50, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String productName,

        @NotBlank(message = "{field.required}")
        @Size(max = 250, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String productDescription,

        @NotNull(message = "{field.required}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{field.price.min}")
        BigDecimal productPrice,

        @NotNull(message = "{field.required}")       // Boolean
        Boolean productEnabled,

        @NotNull(message = "{field.required}")
        @Positive(message = "{field.must.be.positive}")    // Boolean
        Long categoryId  // Solo enviamos el ID de la categoría para asociar
) {}