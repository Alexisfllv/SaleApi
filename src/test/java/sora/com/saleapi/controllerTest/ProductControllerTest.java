package sora.com.saleapi.controllerTest;


// static
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sora.com.saleapi.controller.ProductController;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    // var
    private CategoryDTORequest categoryDTORequest;

    private CategoryDTOResponse categoryDTOResponse;
    private CategoryDTOResponse categoryDTOResponse2;

    private ProductDTORequest productDTORequest;
    private ProductDTOResponse productDTOResponse;
    private ProductDTOResponse productDTOResponse2;

    private List<ProductDTOResponse> productDTOResponseList;

    private String APIPRODUCT = "/api/v1/products";

    @BeforeEach
    public void setup() {

        categoryDTORequest = new CategoryDTORequest("Ropa", "Categoría de ropa", true);

        categoryDTOResponse = new CategoryDTOResponse(1L, "Ropa", "Categoría de ropa", true);
        categoryDTOResponse2 = new CategoryDTOResponse(2L, "Tecnología", "Categoría tecnológica", true);

        productDTORequest = new ProductDTORequest("CamisasTep","Camisas de la marca Tep 2025.",new BigDecimal("49.90"),true,1L);
        productDTOResponse = new ProductDTOResponse(1L,"CamisasTep","Camisas de la marca Tep 2025.",new BigDecimal("49.90"),true,categoryDTOResponse);
        productDTOResponse2 = new ProductDTOResponse(2L,"LaptopDell","Laptop del 2025 Enero",new BigDecimal("3400.90"),true,categoryDTOResponse2);

        productDTOResponseList = List.of(productDTOResponse, productDTOResponse2);

    }
    public static final String MESSAGE_CREATED = "Creado correctamente";
    public static final String MESSAGE_UPDATED = "Actualizado correctamente";
    public static final String MESSAGE_DELETED = "Eliminado correctamente";
    public static final String MESSAGE_NOT_FOUND = "Product not found";

    public static final String ERROR_REQUIRED = "This field is required";
    public static final String ERROR_INVALID_FORMAT = "Invalid format";
    public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";
    public static final String ERROR_SIZE_DESCRIPTION = "The number of items must be between 2 and 250";

    public static final String ERROR_POSITIVE = "The value must be positive";
    public static final String ERROR_PRICE_MIN= "The price must be greater than 0";

    @Nested
    @DisplayName("GET/products")
    class GetProducts {

        // list
        @Test
        @DisplayName("should return full list of products")
        public void shouldReturnFullListofProducts() throws Exception {
            // Arrange
            when(productService.findAll()).thenReturn(productDTOResponseList);
            // Act & Assert
            mockMvc.perform(get(APIPRODUCT))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].productId").value(1L))
                    .andExpect(jsonPath("$[0].productName").value("CamisasTep"))
                    .andExpect(jsonPath("$[1].productId").value(2L))
                    .andExpect(jsonPath("$[1].productName").value("LaptopDell"));

            // Verify
            verify(productService, times(1)).findAll();
        }

        // list page
        @Test
        @DisplayName("should return paginated list of products")
        public void shouldReturnPaginatedListOfProducts() throws Exception {
            //Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("productId").ascending());
            Page<ProductDTOResponse> pageProduct = new PageImpl<>(productDTOResponseList, pageable, productDTOResponseList.size());
            when(productService.findAllPage(any(Pageable.class))).thenReturn(pageProduct);
            // Act & Assert
            mockMvc.perform(get(APIPRODUCT+ "/page")
                            .param("page", "0")
                            .param("size", "5")
                            .param("sort", "clientId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].productId").value(1L))
                    .andExpect(jsonPath("$.content[0].productName").value("CamisasTep"))
                    .andExpect(jsonPath("$.content[1].productId").value(2L))
                    .andExpect(jsonPath("$.content[1].productName").value("LaptopDell"));

            // Verify
            verify(productService, times(1)).findAllPage(any(Pageable.class));
        }

        // list page empty
        @Test
        @DisplayName("should return empty paginated list of products")
        public void shsouldReturnEmptyPaginatedListOfClients() throws Exception {
            //Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("productId").ascending());
            Page<ProductDTOResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(productService.findAllPage(any(Pageable.class))).thenReturn(emptyPage);
            // Act & Assert
            mockMvc.perform(get(APIPRODUCT+ "/page")
                            .param("page", "0")
                            .param("size", "5")
                            .param("sort", "clientId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(0)));

            // Verify
            verify(productService, times(1)).findAllPage(any(Pageable.class));
        }

        // findybyId
        @Test
        @DisplayName("shouldd return product by ID")
        public void shoulddReturnProductByID() throws Exception {
            // Arrange
            Long productId = 1L;
            when(productService.findById(productId)).thenReturn(productDTOResponse);

            // Act & Assert
            mockMvc.perform(get(APIPRODUCT + "/"+ productId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.productId").value(1L))
                    .andExpect(jsonPath("$.productName").value("CamisasTep"))
                    .andExpect(jsonPath("$.productDescription").value("Camisas de la marca Tep 2025."))
                    .andExpect(jsonPath("$.productPrice").value(new BigDecimal("49.90")))
                    .andExpect(jsonPath("$.productEnabled").value(true))
                    .andExpect(jsonPath("$.category.categoryId").value(1))
                    .andExpect(jsonPath("$.category.categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.category.categoryDescription").value("Categoría de ropa"))
                    .andExpect(jsonPath("$.category.categoryEnabled").value(true));

            // Verify
            verify(productService, times(1)).findById(productId);
        }

        // findbyIdNonExist
        @Test
        @DisplayName("should return 404 when product ID does not exist")
        public void shoulddReturn404WhenProductIDDoesNotExist() throws Exception {
            // Arrange
            Long productIdNonExisted = 99L;
            when(productService.findById(productIdNonExisted)).thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));

            // Act & Assert
            mockMvc.perform(get(APIPRODUCT+ "/"+ productIdNonExisted))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT + "/"+ productIdNonExisted))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verify(productService, times(1)).findById(productIdNonExisted);
        }
    }

    @Nested
    @DisplayName("POST/products")
    class PostProducts {

    }

    @Nested
    @DisplayName("PUT/products/{id}")
    class PutProducts {

    }

    @Nested
    @DisplayName("DELETE/products/{id}")
    class DeleteProducts {

    }


}
