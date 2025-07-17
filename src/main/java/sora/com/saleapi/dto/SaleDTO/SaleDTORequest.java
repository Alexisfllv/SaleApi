package sora.com.saleapi.dto.SaleDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;

import java.math.BigDecimal;
import java.util.List;

public record SaleDTORequest(

//        @NotNull
//        @DecimalMin(value = "0.00")
//        BigDecimal saleTotal,
//
//        @NotNull
//        @DecimalMin(value = "0.00")
//        BigDecimal saleTax,

        @NotNull(message = "{field.required}")
        Boolean saleEnabled,

        // fks
        @NotNull(message = "{field.required}")
        @Positive(message = "{field.must.be.positive}")
        Long clientId,

        @NotNull(message = "{field.required}")
        @Positive(message = "{field.must.be.positive}")
        Long userId,

        @NotNull(message = "{field.required}")
        @Size(min = 1, message = "List min 1 ")
        List<@Valid SaleDetailDTORequest> details

) {}
