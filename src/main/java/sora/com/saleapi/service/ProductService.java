package sora.com.saleapi.service;

import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.entity.Product;

import java.util.List;

public interface ProductService {
    // metodos comunes
    List<ProductDTOResponse> findAll();
    ProductDTOResponse findById(Long id);
    ProductDTOResponse save(ProductDTORequest productDTORequest);
    ProductDTOResponse update(Long id, ProductDTORequest productDTORequest);
    void deleteById(Long id);
}
