package sora.com.saleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTOResponse;
import sora.com.saleapi.entity.SaleDetail;

@Mapper (componentModel = "spring")
public interface SaleDetailMapper {

    SaleDetailMapper INSTANCE = Mappers.getMapper(SaleDetailMapper.class);


    // Request
    @Mapping(target = "product.productId", source = "productId")
    SaleDetail toSaleDetail(SaleDetailDTORequest saleDetailDTORequest);


    // Response
    SaleDetailDTOResponse toSaleDetailDTOResponse(SaleDetail saleDetail);
}
