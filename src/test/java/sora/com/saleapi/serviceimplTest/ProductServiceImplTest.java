package sora.com.saleapi.serviceimplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.entity.Category;
import sora.com.saleapi.entity.Product;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.ProductMapper;
import sora.com.saleapi.repo.CategoryRepo;
import sora.com.saleapi.repo.ProductRepo;
import sora.com.saleapi.service.Impl.ProductServiceImpl;
// static
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    // mocks product,category
    @Mock
    private ProductRepo productRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    // variables reutilizables product
    private Product product1;
    private Product product2;
    private Product product3;

    private ProductDTOResponse productDTOResponse1;
    private ProductDTOResponse productDTOResponse2;
    private ProductDTOResponse productDTOResponse3;

    private ProductDTORequest productDTORequest1;
    private Product productRequest1;

    // variables reutilizable categoryService;
    private Category category1;
    private Category category2;
    private Category category3;

    private CategoryDTOResponse categoryDTOResponse1;
    private CategoryDTOResponse categoryDTOResponse2;
    private CategoryDTOResponse categoryDTOResponse3;


    // start
    @BeforeEach
    public void setUp() {
        // inicio
        category1 = new Category(1L,"Ropa","Ropa americana y Europea.", true);
        category2 = new Category(2L, "Tecnología", "Dispositivos electrónicos y accesorios.", true);
        category3 = new Category(3L, "Hogar", "Productos para el hogar y decoración.", true);

        categoryDTOResponse1 = new CategoryDTOResponse(1L,"Ropa","Ropa americana y Europea.", true);
        categoryDTOResponse2 = new CategoryDTOResponse(2L, "Tecnología", "Dispositivos electrónicos y accesorios.", true);
        categoryDTOResponse3 = new CategoryDTOResponse(3L, "Hogar", "Productos para el hogar y decoración.", true);

        // Productos (Entity)
        product1 = new Product(1L, "Laptop-Dell-2022", "Equipo de computacion dell.", new BigDecimal("3400.00"), true, category2);
        product2 = new Product(2L, "Camisa Slim Fit", "Camisa formal de algodón, talla M.", new BigDecimal("120.50"), true, category1);
        product3 = new Product(3L, "Lámpara LED", "Lámpara decorativa para sala, luz cálida.", new BigDecimal("89.90"), true, category3);

        // Productos (DTOResponse)
        productDTOResponse1 = new ProductDTOResponse(1L, "Laptop-Dell-2022", "Equipo de computacion dell.", new BigDecimal("3400.00"), true, categoryDTOResponse2);
        productDTOResponse2 = new ProductDTOResponse(2L, "Camisa Slim Fit", "Camisa formal de algodón, talla M.", new BigDecimal("120.50"), true, categoryDTOResponse1);
        productDTOResponse3 = new ProductDTOResponse(3L, "Lámpara LED", "Lámpara decorativa para sala, luz cálida.", new BigDecimal("89.90"), true, categoryDTOResponse3);

        // Product (Entity) req
        productRequest1 = new Product(null, "Laptop-Dell-2022", "Equipo de computacion dell.", new BigDecimal("3400.00"), true, category2);

        // Product (DTORequest)
        productDTORequest1 = new ProductDTORequest( "Laptop-Dell-2022", "Equipo de computacion dell.", new BigDecimal("3400.00"), true, category2.getCategoryId());
    }

    // test de listado
    @Test
    void  givenProductsExist_whenFindAll_thenReturnAllProducts(){
        // Arrange
        List<Product> products = List.of(product1,product2,product3);
        when(productRepo.findAll()).thenReturn(products);
        when(productMapper.toProductDTOResponse(product1)).thenReturn(productDTOResponse1);
        when(productMapper.toProductDTOResponse(product2)).thenReturn(productDTOResponse2);
        when(productMapper.toProductDTOResponse(product3)).thenReturn(productDTOResponse3);
        // Act
        List<ProductDTOResponse> result = productService.findAll();

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.size()),
                () -> assertEquals(productDTOResponse1,result.get(0)),
                () -> assertEquals(productDTOResponse2,result.get(1)),
                () -> assertEquals(productDTOResponse3,result.get(2))
        );
        verify(productRepo, times(1)).findAll();
        verify(productMapper, times(1)).toProductDTOResponse(product1);
        verify(productMapper, times(1)).toProductDTOResponse(product2);
        verify(productMapper, times(1)).toProductDTOResponse(product3);
        verifyNoMoreInteractions(productRepo, productMapper);
    }

    // listado de productos paginados
    @Test
    void givenProductsExist_whenFindAllPage_thenReturnPagedProducts(){
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product1,product2,product3));
        when(productRepo.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toProductDTOResponse(product1)).thenReturn(productDTOResponse1);
        when(productMapper.toProductDTOResponse(product2)).thenReturn(productDTOResponse2);
        when(productMapper.toProductDTOResponse(product3)).thenReturn(productDTOResponse3);
        // Act
        Page<ProductDTOResponse> result = productService.findAllPage(pageable);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3,result.getContent().size()),
                () -> assertEquals(productDTOResponse1,result.getContent().get(0)),
                () -> assertEquals(productDTOResponse2,result.getContent().get(1)),
                () -> assertEquals(productDTOResponse3,result.getContent().get(2))
        );
        verify(productRepo, times(1)).findAll(pageable);
        verify(productMapper, times(1)).toProductDTOResponse(product1);
        verify(productMapper, times(1)).toProductDTOResponse(product2);
        verify(productMapper, times(1)).toProductDTOResponse(product3);
        verifyNoMoreInteractions(productRepo, productMapper);
    }

    // listado vacio paginado
    @Test
    void givenNoProducts_whenFindAllPage_thenReturnEmptyPage(){
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of());
        when(productRepo.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<ProductDTOResponse> result = productService.findAllPage(pageable);
        // Assert & Verify
        assertAll(
                () -> assertTrue(result.isEmpty()),
                () -> assertEquals(0,result.getContent().size())
        );
        verify(productRepo, times(1)).findAll(pageable);
        verifyNoMoreInteractions(productRepo);
    }

    // busqueda de producto por id
    @Test
    void givenProductExists_whenFindById_thenReturnProduct(){
        // Arrange
        Long productId = 1L;
        Product productExist = product1;
        when(productRepo.findById(productId)).thenReturn(Optional.of(productExist));
        when(productMapper.toProductDTOResponse(productExist)).thenReturn(productDTOResponse1);

        // Act
        ProductDTOResponse result = productService.findById(productId);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L,result.productId()),
                () -> assertEquals("Laptop-Dell-2022",result.productName()),
                () -> assertEquals("Equipo de computacion dell.",result.productDescription()),
                () -> assertEquals(new BigDecimal("3400.00"),result.productPrice()),
                () -> assertEquals(true,result.productEnabled()),
                () -> assertEquals(2L,result.category().categoryId()),
                () -> assertEquals("Tecnología",result.category().categoryName()),
                () -> assertEquals("Dispositivos electrónicos y accesorios.",result.category().categoryDescription()),
                () -> assertEquals(true,result.category().categoryEnabled())
        );
        verify(productRepo, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepo);
    }

    // test id no encontrado producto
    @Test
    void givenProductNotExist_whenFindById_thenThrowException(){
        // Arrange
        Long productIdNonExist = 1L;
        when(productRepo.findById(productIdNonExist)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.findById(productIdNonExist));
        //  & Verify
        verify(productRepo, times(1)).findById(productIdNonExist);
        verifyNoMoreInteractions(productRepo);
    }

    // test de save producto
    @Test
    void givenValidProductRequest_whenSave_thenReturnSavedProduct(){
        // Arrange
            // valor id null
        ProductDTORequest productDTOReq = productDTORequest1;
        Product productRe = productRequest1;
        Long categoryId = 2L;
        when(productMapper.toProduct(productDTOReq)).thenReturn(productRe);
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category2));
        when(productRepo.save(any(Product.class))).thenReturn(product1);
        when(productMapper.toProductDTOResponse(any(Product.class))).thenReturn(productDTOResponse1);

        // Act
        ProductDTOResponse result = productService.save(productDTOReq);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L,result.productId()),
                () -> assertEquals("Laptop-Dell-2022",result.productName()),
                () -> assertEquals("Equipo de computacion dell.",result.productDescription()),
                () -> assertEquals(new BigDecimal("3400.00"),result.productPrice()),
                () -> assertEquals(true,result.productEnabled()),
                () -> assertEquals(2L,result.category().categoryId()),
                () -> assertEquals("Tecnología",result.category().categoryName()),
                () -> assertEquals("Dispositivos electrónicos y accesorios.",result.category().categoryDescription()),
                () -> assertEquals(true,result.category().categoryEnabled())
        );
        verify(productMapper,times(1)).toProduct(any(ProductDTORequest.class));
        verify(categoryRepo, times(1)).findById(categoryId);
        verify(productRepo, times(1)).save(any(Product.class));
        verify(productMapper,times(1)).toProductDTOResponse(any(Product.class));
        verifyNoMoreInteractions(categoryRepo,productRepo,productMapper);
    }

    // update test product
    @Test
    void givenProductExists_whenUpdate_thenReturnUpdatedProduct(){
        // Arrange
        Long productId = 1L;
        ProductDTORequest productDTOUpdate = new ProductDTORequest("Gorra Gorin","Gorra gorin bros gallo rojo.",new BigDecimal("150.20"),true,category1.getCategoryId());
        Long categoryId = 1L;
        Product productUpdate = new Product(1L,"Gorra Gorin","Gorra gorin bros gallo rojo.",new BigDecimal("150.20"),true,category1);
        ProductDTOResponse productDTORes = new ProductDTOResponse(1L,"Gorra Gorin","Gorra gorin bros gallo rojo.",new BigDecimal("150.20"),true,categoryDTOResponse1);

        when(productRepo.findById(productId)).thenReturn(Optional.of(product1));
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category1));
        when(productRepo.save(any(Product.class))).thenReturn(productUpdate);
        when(productMapper.toProductDTOResponse(any(Product.class))).thenReturn(productDTORes);

        // Act
        ProductDTOResponse result = productService.update(productId, productDTOUpdate);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L,result.productId()),
                () -> assertEquals("Gorra Gorin",result.productName()),
                () -> assertEquals("Gorra gorin bros gallo rojo.",result.productDescription()),
                () -> assertEquals(new BigDecimal("150.20"),result.productPrice()),
                () -> assertEquals(true,result.productEnabled()),
                () -> assertEquals(1L,result.category().categoryId()),
                () -> assertEquals("Ropa",result.category().categoryName()),
                () -> assertEquals("Ropa americana y Europea.",result.category().categoryDescription()),
                () -> assertEquals(true,result.category().categoryEnabled())
        );
        verify(productRepo, times(1)).findById(productId);
        verify(categoryRepo, times(1)).findById(categoryId);
        verify(productRepo, times(1)).save(any(Product.class));
        verify(productMapper,times(1)).toProductDTOResponse(any(Product.class));
        verifyNoMoreInteractions(categoryRepo,productRepo,productMapper);
    }

    // update fallido por id de product
    @Test
    void givenProductNotExist_whenUpdate_thenThrowException(){
        // Arrange
        Long productIdNonExist = 99L;
        ProductDTORequest productDTOUpdate = new ProductDTORequest("Gorra Gorin","Gorra gorin bros gallo rojo.",new BigDecimal("150.20"),true,category1.getCategoryId());
        when(productRepo.findById(productIdNonExist)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.update(productIdNonExist,productDTOUpdate));

        // Verify
        verify(productRepo, times(1)).findById(productIdNonExist);
        verifyNoMoreInteractions(productRepo);
    }

    // update fallido por id de category
    @Test
    void givenCategoryNotExist_whenUpdate_thenThrowException(){
        // Arrange
        Long productId = 1L;
        Long categoryIdNonExist = 99L;
        ProductDTORequest productDTOUpdate = new ProductDTORequest("Gorra Gorin","Gorra gorin bros gallo rojo.",new BigDecimal("150.20"),true,categoryIdNonExist);
        when(productRepo.findById(productId)).thenReturn(Optional.of(product1));
        when(categoryRepo.findById(categoryIdNonExist)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.update(productId,productDTOUpdate));

        // Verify
        verify(productRepo, times(1)).findById(productId);
        verify(categoryRepo, times(1)).findById(categoryIdNonExist);
        verifyNoMoreInteractions(categoryRepo,productRepo);
    }

    // delete product
    @Test
    void givenProductExists_whenDelete_thenRemoveProduct(){
        // Arrange
        Long productId = 1L;
        when(productRepo.findById(productId)).thenReturn(Optional.of(product1));
        // Act
        productService.deleteById(productId);
        // Assert & Verify
        verify(productRepo, times(1)).findById(productId);
        verify(productRepo, times(1)).delete(product1);
        verifyNoMoreInteractions(productRepo);
    }
    // delete product non exist fail
    @Test
    void givenProductNotExist_whenDelete_thenThrowException(){
        // Arrange
        Long productIdNonExist = 99L;
        when(productRepo.findById(productIdNonExist)).thenReturn(Optional.empty());
        // Act
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteById(productIdNonExist));
        // Assert & Verify
        verify(productRepo, times(1)).findById(productIdNonExist);
        verifyNoMoreInteractions(productRepo);
    }
}
