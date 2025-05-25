package sora.com.saleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.entity.Sale;

@Mapper (componentModel = "spring", uses = {SaleDetailMapper.class})
public interface SaleMapper {

    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

    // Request

    @Mapping(target = "client.clientId", source = "clientId")
    @Mapping(target = "user.userId", source = "userId")
    Sale toSale(SaleDTORequest saleDTORequest);


    // Response

    SaleDTOResponse toSaleDTOResponse(Sale sale);
}
