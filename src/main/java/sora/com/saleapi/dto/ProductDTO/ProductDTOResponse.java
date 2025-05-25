package sora.com.saleapi.dto.ProductDTO;

import java.math.BigDecimal;

public record ProductDTOResponse(
        Long productId,
        String productName,
        String productDescription,
        BigDecimal productPrice,
        Boolean productEnabled,
        Long categoryId,
        String categoryName
) {}
