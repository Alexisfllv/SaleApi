package sora.com.saleapi.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.entity.Category;
import sora.com.saleapi.entity.Product;
import sora.com.saleapi.mapper.ProductMapper;
import sora.com.saleapi.repo.CategoryRepo;
import sora.com.saleapi.repo.ProductRepo;
import sora.com.saleapi.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    // ioc
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductMapper productMapper;


    @Override
    public List<ProductDTOResponse> findAll() {
        List<Product> lista = productRepo.findAll();
        return lista.stream()
                .map(product -> productMapper.toProductDTOResponse(product))
                .toList();

    }

    @Override
    public Page<ProductDTOResponse> findAllPage(Pageable pageable) {
        Page<Product> lista = productRepo.findAll(pageable);
        return lista.map(product -> productMapper.toProductDTOResponse(product));
    }

    @Override
    public ProductDTOResponse findById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Product not found"));
        return productMapper.toProductDTOResponse(product);
    }

    @Override
    public ProductDTOResponse save(ProductDTORequest productDTORequest) {
        Product product = productMapper.toProduct(productDTORequest);

        // category
        Category category =  categoryRepo.findById(productDTORequest.categoryId())
                .orElseThrow( () -> new RuntimeException("Category not found"));
        product.setCategory(category);
        productRepo.save(product);

        return productMapper.toProductDTOResponse(product);
    }

    @Override
    public ProductDTOResponse update(Long id, ProductDTORequest productDTORequest) {
        Product product = productRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Product not found"));

        product.setProductName(productDTORequest.productName());
        product.setProductDescription(productDTORequest.productDescription());
        product.setProductEnabled(productDTORequest.productEnabled());

        // category

        Category category =  categoryRepo.findById(productDTORequest.categoryId())
                        .orElseThrow( () -> new RuntimeException("Category not found"));

        product.setCategory(category);
        productRepo.save(product);
        return productMapper.toProductDTOResponse(product);
    }

    @Override
    public void deleteById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Product not found"));
        productRepo.delete(product);
    }
}
