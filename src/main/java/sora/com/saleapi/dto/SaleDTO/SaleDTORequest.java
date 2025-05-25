package sora.com.saleapi.dto.SaleDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;

import java.math.BigDecimal;
import java.util.List;

public record SaleDTORequest(

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal saleTotal,

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal saleTax,

        @NotNull
        Boolean saleEnabled,

        // fks
        @NotNull
        Long clientId,

        @NotNull
        Long userId,

        @NotNull
        List<SaleDetailDTORequest> details

) {}
