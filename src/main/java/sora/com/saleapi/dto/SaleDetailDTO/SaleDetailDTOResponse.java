package sora.com.saleapi.dto.SaleDetailDTO;

import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.entity.Product;

import java.math.BigDecimal;

public record SaleDetailDTOResponse(
        Long saleDetailId,
        BigDecimal discount,
        Integer quantity,
        BigDecimal salePrice,
        ProductDTOResponse product,
        Long  saleId
) {}
