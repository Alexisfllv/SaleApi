package sora.com.saleapi.controller;


import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<CategoryDTOResponse>> findAll(){
        List<CategoryDTOResponse> lista = categoryService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTOResponse> findById(@PathVariable ("id") Long id){
        CategoryDTOResponse categoryDTOResponse = categoryService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTOResponse);
    }

    @PostMapping
    public ResponseEntity<CategoryDTOResponse> save( @Valid @RequestBody CategoryDTORequest categoryDTORequest){
        CategoryDTOResponse categoryDTOResponse = categoryService.save(categoryDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTOResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTOResponse> update(@PathVariable ("id") Long id, @Valid @RequestBody CategoryDTORequest categoryDTORequest){
        CategoryDTOResponse categoryDTOResponse = categoryService.update(id, categoryDTORequest);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTOResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable ("id") Long id){
        categoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
