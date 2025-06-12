package sora.com.saleapi.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sora.com.saleapi.apiResponse.GenericResponse;
import sora.com.saleapi.apiResponse.StatusApi;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    //ioc
    private final CategoryService categoryService;

    // metodos

    @GetMapping
    public ResponseEntity<GenericResponse<CategoryDTOResponse>> findAll(){
        List<CategoryDTOResponse> lista = categoryService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(StatusApi.SUCCESS,lista));
    }
    @GetMapping("/page")
    public ResponseEntity<Page<CategoryDTOResponse>> findAllPage(
            @PageableDefault (
                    page = 0 ,
                    size = 10,
                    sort = "categoryId",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ){
        Page<CategoryDTOResponse> page = categoryService.findAllPage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<CategoryDTOResponse>> findById(@PathVariable ("id") Long id){
        CategoryDTOResponse categoryDTOResponse = categoryService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(StatusApi.SUCCESS,List.of(categoryDTOResponse)));
    }

    @PostMapping
    public ResponseEntity<GenericResponse<CategoryDTOResponse>> save( @Valid @RequestBody CategoryDTORequest categoryDTORequest){
        CategoryDTOResponse categoryDTOResponse = categoryService.save(categoryDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericResponse<>(StatusApi.CREATED,List.of(categoryDTOResponse)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<CategoryDTOResponse>> update(@PathVariable ("id") Long id, @Valid @RequestBody CategoryDTORequest categoryDTORequest){
        CategoryDTOResponse categoryDTOResponse = categoryService.update(id, categoryDTORequest);
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(StatusApi.UPDATED,List.of(categoryDTOResponse)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteById(@PathVariable ("id") Long id){
        categoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(StatusApi.DELETED));
    }

}
