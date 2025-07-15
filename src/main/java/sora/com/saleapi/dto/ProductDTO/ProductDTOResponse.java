package sora.com.saleapi.dto.ProductDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;

import java.math.BigDecimal;

public record ProductDTOResponse(
        Long productId,
        String productName,
        String productDescription,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
        BigDecimal productPrice,
        Boolean productEnabled,
        CategoryDTOResponse category
) {}
