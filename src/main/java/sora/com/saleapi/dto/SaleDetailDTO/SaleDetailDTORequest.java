package sora.com.saleapi.dto.SaleDetailDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaleDetailDTORequest(

//        @NotNull
//        @DecimalMin(value = "0.00")
//        BigDecimal discount,

        @NotNull
        @Min(1)
        Integer quantity,

//        @NotNull
//        @DecimalMin(value = "0.00")
//        BigDecimal salePrice,

        @NotNull
        Long productId

) {}
