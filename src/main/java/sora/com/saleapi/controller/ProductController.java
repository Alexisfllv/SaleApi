package sora.com.saleapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    //ioc
    private final ProductService productService;

    // metodos

    @GetMapping
    public ResponseEntity<List<ProductDTOResponse>> findAll(){
        List<ProductDTOResponse> lista = productService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> findById(@PathVariable("id") Long id){
        ProductDTOResponse productDTOResponse = productService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(productDTOResponse);
    }

    @PostMapping
    public ResponseEntity<ProductDTOResponse> save( @Valid @RequestBody ProductDTORequest productDTORequest){
        ProductDTOResponse productDTOResponse = productService.save(productDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTOResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> update(@PathVariable ("id") Long id, @Valid @RequestBody ProductDTORequest productDTORequest){
        ProductDTOResponse productDTOResponse = productService.update(id, productDTORequest);
        return ResponseEntity.status(HttpStatus.OK).body(productDTOResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable ("id") Long id){
        productService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
