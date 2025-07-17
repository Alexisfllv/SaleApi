package sora.com.saleapi.dto.SaleDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTOResponse;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;

import java.math.BigDecimal;
import java.util.List;

public record SaleDTOResponse(
        Long saleId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
        BigDecimal saleTotal,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
        BigDecimal saleTax,
        Boolean saleEnabled,
        //
        ClientDTOResponse client,
        UserDTOResponse user,
        List<SaleDetailDTOResponse> details
) {}
