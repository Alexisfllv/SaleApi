package sora.com.saleapi.dto.SaleDetailDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.entity.Product;

import java.math.BigDecimal;

public record SaleDetailDTOResponse(
        Long saleDetailId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
        BigDecimal discount,
        Integer quantity,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
        BigDecimal salePrice,
        ProductDTOResponse product,
        Long  saleId
) {}
