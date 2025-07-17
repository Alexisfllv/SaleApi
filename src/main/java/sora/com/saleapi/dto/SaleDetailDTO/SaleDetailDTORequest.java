package sora.com.saleapi.dto.SaleDetailDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SaleDetailDTORequest(

//        @NotNull
//        @DecimalMin(value = "0.00")
//        BigDecimal discount,

        @NotNull(message = "{field.required}")
        @Positive(message = "{field.must.be.positive}")
        Integer quantity,

//        @NotNull
//        @DecimalMin(value = "0.00")
//        BigDecimal salePrice,

        @NotNull(message = "{field.required}")
        @Positive(message = "{field.must.be.positive}")
        Long productId

) {}
