package sora.com.saleapi.dto.ProductDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductDTORequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50)
        String productName,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 250)
        String productDescription,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
        BigDecimal productPrice,

        @NotNull(message = "El estado habilitado es obligatorio")
        Boolean productEnabled,

        @NotNull(message = "La categoría es obligatoria")
        Long categoryId  // Solo enviamos el ID de la categoría para asociar
) {}