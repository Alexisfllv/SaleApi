package sora.com.saleapi.dto.ProductDTO;

import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;

import java.math.BigDecimal;

public record ProductDTOResponse(
        Long productId,
        String productName,
        String productDescription,
        BigDecimal productPrice,
        Boolean productEnabled,
        CategoryDTOResponse category
) {}
