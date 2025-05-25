package sora.com.saleapi.dto.SaleDTO;

import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTOResponse;

import java.math.BigDecimal;
import java.util.List;

public record SaleDTOResponse(
        Long saleId,
        BigDecimal saleTotal,
        BigDecimal saleTax,
        Boolean saleEnabled,
        Long clientId,
        String clientFullName,
        Long userId,
        String userName,
        List<SaleDetailDTOResponse> details
) {}
