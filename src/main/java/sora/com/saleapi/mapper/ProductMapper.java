package sora.com.saleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.entity.Product;

@Mapper (componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);


    // Request
    @Mapping(target = "category.categoryId", source = "categoryId")
    Product toProduct(ProductDTORequest productDTORequest);

    // Response
    ProductDTOResponse toProductDTOResponse(Product product);
}
