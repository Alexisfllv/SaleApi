package sora.com.saleapi.dto.CategoryDTO;



public record CategoryDTOResponse(
        Long categoryId,
        String categoryName,
        String categoryDescription,
        Boolean categoryEnabled
) { }
