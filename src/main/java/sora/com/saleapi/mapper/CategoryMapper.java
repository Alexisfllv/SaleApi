package sora.com.saleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.entity.Category;

@Mapper (componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Request
    Category toCategory(CategoryDTORequest categoryDTORequest);
    // Response
    CategoryDTOResponse toCategoryDTOResponse(Category category);


}
