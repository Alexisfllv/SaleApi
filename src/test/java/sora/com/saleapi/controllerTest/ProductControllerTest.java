package sora.com.saleapi.controllerTest;


// static
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    private CategoryDTOResponse categoryDTOResponse;

    private ProductDTORequest productDTORequest;
    private ProductDTOResponse productDTOResponse;
    private ProductDTOResponse productDTOResponse2;

    private List<ProductDTOResponse> productDTOResponseList;

    private String APIPRODUCT = "/api/v1/products";

    @BeforeEach
    public void setup() {

        categoryDTOResponse = new CategoryDTOResponse(1L, "Ropa", "Categoría de ropa", true);

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
                    .andExpect(jsonPath("$.error").value("Not Found"))
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

        @Test
        @DisplayName("should create product successfully")
        void shouldCreateProduct() throws Exception {
            // Arrange
            when(productService.save(any(ProductDTORequest.class))).thenReturn(productDTOResponse);
            // Act & Assert
            mockMvc.perform(post(APIPRODUCT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productDTORequest)))
                    .andExpect(status().isCreated())
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
            verify(productService, times(1)).save(any(ProductDTORequest.class));
        }

        // @Valid productName
        @ParameterizedTest
        @DisplayName("should return 400 when productName is invalid")
        @MethodSource("provideInvalidProductName")
        void shouldReturn400WhenProductNameIsInvalid(String invalid, String expected) throws Exception {
            // Arrange
            String json = """
                    {
                      "productName": "%s",
                      "productDescription": "Radio de los 90s",
                      "productPrice": 29.99,
                      "productEnabled": true,
                      "categoryId": 1
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIPRODUCT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productName")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT))
                    .andExpect(jsonPath("$.timestamp").exists());


            // Verify
            verifyNoMoreInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidProductName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid productDescription
        @ParameterizedTest
        @DisplayName("should return 400 when productDescription is invalid")
        @MethodSource("provideInvalidProductDescription")
        void shouldReturn400WhenProductDescriptionIsInvalid(String invalid, String expected) throws Exception {
            // Arrange
            String json = """
                    {
                      "productName": "nameRopa",
                      "productDescription": "%s",
                      "productPrice": 29.99,
                      "productEnabled": true,
                      "categoryId": 1
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIPRODUCT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productDescription")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT))
                    .andExpect(jsonPath("$.timestamp").exists());


            // Verify
            verifyNoMoreInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidProductDescription() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_DESCRIPTION),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid productPrice
        @ParameterizedTest
        @DisplayName("should return 400 when productPrice is invalid")
        @MethodSource("provideInvalidProductPrice")
        void shouldReturn400WhenProductPriceIsInvalid(String invalid, String expected) throws Exception {
            // Arrange
            String json = """
                    {
                      "productName": "nameRopa",
                      "productDescription": "Description Ropa",
                      "productPrice": %s,
                      "productEnabled": true,
                      "categoryId": 1
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIPRODUCT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productPrice")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT))
                    .andExpect(jsonPath("$.timestamp").exists());


            // Verify
            verifyNoMoreInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidProductPrice() {
            return Stream.of(
                    Arguments.of("null",ERROR_REQUIRED),
                    Arguments.of("0.0",ERROR_PRICE_MIN)
            );
        }

        // @Valid productEnabled
        @Test
        @DisplayName("should return 400 when productEnabled is invalid")
        void shouldReturn400WhenProductEnabledIsInvalid() throws Exception {
            // Arrange
            String json = """
                    {
                      "productName": "nameRopa",
                      "productDescription": "Description Ropa",
                      "productPrice": 99.90,
                      "productEnabled": null,
                      "categoryId": 1
                    }
                    """;
            // Act & Assert
            mockMvc.perform(post(APIPRODUCT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productEnabled")))
                    .andExpect(jsonPath("$.message").value(containsString(ERROR_REQUIRED)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoMoreInteractions(productService);
        }

        // @Valid categoryId
        @ParameterizedTest
        @DisplayName("should return 400 when productCategoryId is invalid")
        @MethodSource("provideInvalidProductCategoryId")
        void shouldReturn400WhenProductCategoryIdIsInvalid(String invalid, String expected) throws Exception {
            // Arrange
            String json = """
                    {
                      "productName": "nameRopa",
                      "productDescription": "Description Ropa",
                      "productPrice": 99.90,
                      "productEnabled": true,
                      "categoryId": %s
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(post(APIPRODUCT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryId")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT))
                    .andExpect(jsonPath("$.timestamp").exists());


            // Verify
            verifyNoInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidProductCategoryId() {
            return Stream.of(
                    Arguments.of("null",ERROR_REQUIRED),
                    Arguments.of("-1",ERROR_POSITIVE)
            );
        }

        // JSON BAD FORMAT
        @Test
        @DisplayName("should return 400 when JSON  is malformed")
        void shouldReturnMalformedJsonError() throws Exception {
            // Arrange
            String json = """
                    {
                      "productName": "nameRopa",
                      "productDescription": "Description Ropa",
                      "productPrice": 99.90,
                      "productEnabled": null,
                      "categoryId": yes
                    }
                    """;
            // Act & Assert
            mockMvc.perform(post(APIPRODUCT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value(containsString("JSON bad format.")))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(productService);
        }

    }

    @Nested
    @DisplayName("PUT/products/{id}")
    class PutProducts {

        //success
        @Test
        @DisplayName("should update product successfully")
        void shouldUpdateProductSuccessfully() throws Exception {
            // Arrange
            Long productId = 1L;
            ProductDTORequest updateRequest = new ProductDTORequest("Cassacas","Casacas de la marca Tep 2025.",new BigDecimal("90.90"),true,1L);
            ProductDTOResponse updateResponse  = new ProductDTOResponse(1L,"Cassacas","Casacas de la marca Tep 2025.",new BigDecimal("90.90"),true,categoryDTOResponse);
            when(productService.update(eq(productId), eq(updateRequest))).thenReturn(updateResponse);

            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.productId").value(1L))
                    .andExpect(jsonPath("$.productName").value("Cassacas"))
                    .andExpect(jsonPath("$.productDescription").value("Casacas de la marca Tep 2025."))
                    .andExpect(jsonPath("$.productPrice").value(new BigDecimal("90.90")))
                    .andExpect(jsonPath("$.productEnabled").value(true))
                    .andExpect(jsonPath("$.category.categoryId").value(1))
                    .andExpect(jsonPath("$.category.categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.category.categoryDescription").value("Categoría de ropa"))
                    .andExpect(jsonPath("$.category.categoryEnabled").value(true));

            // Verify
            verify(productService,times(1)).update(eq(productId), any(ProductDTORequest.class));
        }

        // update non exist id
        @Test
        @DisplayName("shoukd return 404 when updating non-existent product")
        void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
            // Arrange
            Long productIdNonExist = 99L;
            ProductDTORequest updateRequest = new ProductDTORequest("Cassacas","Casacas de la marca Tep 2025.",new BigDecimal("90.90"),true,1L);

            when(productService.update(eq(productIdNonExist),any(ProductDTORequest.class))).thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));
            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/" + productIdNonExist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productIdNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(productService,times(1)).update(eq(productIdNonExist), any(ProductDTORequest.class));
            verifyNoMoreInteractions(productService);
        }

        // @Valid productName
        @ParameterizedTest
        @DisplayName("should return 400 when productName is invalid on update")
        @MethodSource("provideInvalidproductName")
        void shouldReturn400WhenProductNameIsInvalidUpdate(String invalid, String expected) throws Exception {
            // Arrange
            Long productId = 1L;
            String json = """
                    {
                      "productName": "%s",
                      "productDescription": "productDescriptionRopa",
                      "productPrice": 90.90,
                      "productEnabled": true,
                      "categoryId": 1
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productName")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidproductName() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_NAME),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid productDescription
        @ParameterizedTest
        @DisplayName("should return 400 when productDescription is invalid on update")
        @MethodSource("provideInvalidproductDescription")
        void shouldReturn400WhenProductDescriptionIsInvalidUpdate(String invalid, String expected) throws Exception {
            // Arrange
            Long productId = 1L;
            String json = """
                    {
                      "productName": "productName",
                      "productDescription": "%s",
                      "productPrice": 90.90,
                      "productEnabled": true,
                      "categoryId": 1
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productDescription")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidproductDescription() {
            return Stream.of(
                    Arguments.of("",ERROR_REQUIRED),
                    Arguments.of("A",ERROR_SIZE_DESCRIPTION),
                    Arguments.of("' OR '1'='1",ERROR_INVALID_FORMAT)
            );
        }

        // @Valid productPrice
        @ParameterizedTest
        @DisplayName("should return 400 when productPrice is invalid on update")
        @MethodSource("provideInvalidproductPrice")
        void shouldReturn400WhenProductPriceIsInvalidUpdate(String invalid, String expected) throws Exception {
            // Arrange
            Long productId = 1L;
            String json = """
                    {
                      "productName": "productName",
                      "productDescription": "productDescription",
                      "productPrice": %s,
                      "productEnabled": true,
                      "categoryId": 1
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productPrice")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidproductPrice() {
            return Stream.of(
                    Arguments.of("null",ERROR_REQUIRED),
                    Arguments.of("0.0",ERROR_PRICE_MIN)
            );
        }

        // @Valid productEnabled
        @Test
        @DisplayName("should return 400 when productEnabled is invalid on update")
        void shouldReturn400WhenProductEnabledIsInvalidUpdate() throws Exception {
            // Arrange
            Long productId = 1L;
            String json = """
                    {
                      "productName": "productName",
                      "productDescription": "productDescription",
                      "productPrice": 99.90,
                      "productEnabled": null,
                      "categoryId": 1
                    }
                    """;
            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("productEnabled")))
                    .andExpect(jsonPath("$.message").value(containsString(ERROR_REQUIRED)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(productService);
        }

        // @Valid productCategoryId
        @ParameterizedTest
        @DisplayName("should return 400 when productCategoryId is invalid on update")
        @MethodSource("provideInvalidproductCategoryId")
        void shouldReturn400WhenProductCategoryIdIsInvalidUpdate(String invalid, String expected) throws Exception {
            // Arrange
            Long productId = 1L;
            String json = """
                    {
                      "productName": "productName",
                      "productDescription": "productDescription",
                      "productPrice": 99.90,
                      "productEnabled": true,
                      "categoryId": %s
                    }
                    """.formatted(invalid);
            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryId")))
                    .andExpect(jsonPath("$.message").value(containsString(expected)))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(productService);
        }

        // method
        private static Stream<Arguments> provideInvalidproductCategoryId() {
            return Stream.of(
                    Arguments.of("null",ERROR_REQUIRED),
                    Arguments.of("-1",ERROR_POSITIVE)
            );
        }

        // JSON BAD FORMAT
        @Test
        @DisplayName("should return 400 when JSON  is malformed on update")
        void shouldReturnMalformedJsonErrorOnUpdate() throws Exception {
            // Arrange
            Long productId = 1L;
            String json = """
                    {
                      "productName": "nameRopa",
                      "productDescription": "Description Ropa",
                      "productPrice": 99.90,
                      "productEnabled": null,
                      "categoryId": yes
                    }
                    """;
            // Act & Assert
            mockMvc.perform(put(APIPRODUCT+"/"+productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value(containsString("JSON bad format.")))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productId))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(productService);
        }




    }

    @Nested
    @DisplayName("DELETE/products/{id}")
    class DeleteProducts {

        // Delete by Id Success
        @Test
        @DisplayName("should delete product successfully")
        void shouldDeleteProductSuccessfully() throws Exception {
            // Arrange
            Long productId = 1L;
            doNothing().when(productService).deleteById(productId);
            // Act & Assert
            mockMvc.perform(delete(APIPRODUCT+"/{id}", productId))
                    .andExpect(status().isNoContent());

            // Verify
            verify(productService).deleteById(productId);
        }

        // Delete ById Success
        @Test
        @DisplayName("should return 404 wjen deleting non-existent product")
        void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
            // Arrange
            Long productIdNonExist = 99L;
            doThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND)).when(productService).deleteById(productIdNonExist);
            // Act & Assert
            mockMvc.perform(delete(APIPRODUCT+"/" + productIdNonExist))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.message").value(MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.path").value(APIPRODUCT+"/"+productIdNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(productService).deleteById(productIdNonExist);
        }


    }


}
