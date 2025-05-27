package sora.com.saleapi.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.entity.Category;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.CategoryMapper;
import sora.com.saleapi.repo.CategoryRepo;
import sora.com.saleapi.service.CategoryService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    // ioc
    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;


    @Override
    public List<CategoryDTOResponse> findAll() {
        List<Category> lista = categoryRepo.findAll();
        return lista.stream()
                .map(category -> categoryMapper.toCategoryDTOResponse(category))
                .toList();
    }

    @Override
    public CategoryDTOResponse findById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toCategoryDTOResponse(category);
    }

    @Override
    public CategoryDTOResponse save(CategoryDTORequest categoryDTORequest) {
        Category category = categoryMapper.toCategory(categoryDTORequest);
        categoryRepo.save(category);
        return categoryMapper.toCategoryDTOResponse(category);
    }

    @Override
    public CategoryDTOResponse update(Long id, CategoryDTORequest categoryDTORequest) {
        Category category = categoryRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Category not found"));

        category.setCategoryName(categoryDTORequest.categoryName());
        category.setCategoryDescription(categoryDTORequest.categoryDescription());
        category.setCategoryEnabled(categoryDTORequest.categoryEnabled());
        categoryRepo.save(category);
        return categoryMapper.toCategoryDTOResponse(category);
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Category not found"));
        categoryRepo.delete(category);
    }
}
