package sora.com.saleapi.service;

import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;

import java.util.List;

public interface CategoryService {

    // metodos comunes
    List<CategoryDTOResponse> findAll();
    CategoryDTOResponse findById(Long id);
    CategoryDTOResponse save(CategoryDTORequest categoryDTORequest);
    CategoryDTOResponse update(Long id, CategoryDTORequest categoryDTORequest);
    void deleteById(Long id);
}
