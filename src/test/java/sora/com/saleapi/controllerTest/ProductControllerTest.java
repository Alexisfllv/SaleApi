package sora.com.saleapi.controllerTest;


// static
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sora.com.saleapi.controller.ProductController;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.service.ProductService;

import java.math.BigDecimal;
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
        productDTOResponse2 = new ProductDTOResponse(1L,"Laptop Dell","Laptop del 2025 Enero",new BigDecimal("3400.90"),true,categoryDTOResponse2);

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
