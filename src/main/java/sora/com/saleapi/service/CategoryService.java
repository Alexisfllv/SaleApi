package sora.com.saleapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;

import java.util.List;

public interface CategoryService {

    // metodos comunes
    List<CategoryDTOResponse> findAll();
    Page<CategoryDTOResponse> findAllPage(Pageable pageable);
    CategoryDTOResponse findById(Long id);
    CategoryDTOResponse save(CategoryDTORequest categoryDTORequest);
    CategoryDTOResponse update(Long id, CategoryDTORequest categoryDTORequest);
    void deleteById(Long id);
}
