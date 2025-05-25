package sora.com.saleapi.dto.SaleDTO;

import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTOResponse;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;

import java.math.BigDecimal;
import java.util.List;

public record SaleDTOResponse(
        Long saleId,
        BigDecimal saleTotal,
        BigDecimal saleTax,
        Boolean saleEnabled,
        //
        ClientDTOResponse client,
        UserDTOResponse user,
        List<SaleDetailDTOResponse> details
) {}
