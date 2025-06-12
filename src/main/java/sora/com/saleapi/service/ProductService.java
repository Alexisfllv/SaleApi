package sora.com.saleapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.entity.Product;

import java.util.List;

public interface ProductService {
    // metodos comunes
    List<ProductDTOResponse> findAll();
    Page<ProductDTOResponse> findAllPage(Pageable pageable);
    ProductDTOResponse findById(Long id);
    ProductDTOResponse save(ProductDTORequest productDTORequest);
    ProductDTOResponse update(Long id, ProductDTORequest productDTORequest);
    void deleteById(Long id);
}
