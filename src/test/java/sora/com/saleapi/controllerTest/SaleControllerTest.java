package sora.com.saleapi.controllerTest;




// static
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sora.com.saleapi.controller.SaleController;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.dto.ProductDTO.ProductDTORequest;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTOResponse;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.service.SaleService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.rmi.server.LogStream.log;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebMvcTest(controllers = SaleController.class)
public class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService saleService;

    @Autowired
    private ObjectMapper objectMapper;

    // var
    private CategoryDTOResponse categoryDTOResponse;
    private ProductDTOResponse productDTOResponse;
    private ClientDTOResponse clientDTOResponse;
    private RoleDTOResponse roleDTOResponse;
    private UserDTOResponse userDTOResponse;

    private SaleDTORequest saleDTORequest;
    private SaleDTOResponse saleDTOResponse;
    private SaleDTOResponse saleDTOResponse2;

    private List<SaleDTOResponse> saleDTOResponsesList;

    private SaleDetailDTORequest saleDetailDTORequest;
    private SaleDetailDTOResponse saleDetailDTOResponse;

    private List<SaleDetailDTORequest> saleDetailDTORequestsList;
    private List<SaleDetailDTOResponse> saleDetailDTOResponsesList;

    private String APISALES = "/api/v1/sales";

    @BeforeEach
    public void setup() {
        categoryDTOResponse = new CategoryDTOResponse(1L, "Ropa", "Categoría de ropa", true);
        productDTOResponse = new ProductDTOResponse(1L,"CamisasTep","Camisas de la marca Tep 2025.",new BigDecimal("49.90"),true,categoryDTOResponse);
        clientDTOResponse = new ClientDTOResponse(1L, "Juan", "Pérez", "juan.perez@example.com", "ID12345699", "987654321", "Av. Siempre Viva 742");
        roleDTOResponse = new RoleDTOResponse(1L,"ADMIN",true);
        userDTOResponse = new UserDTOResponse(1L,"Alexis",true,roleDTOResponse);

        //sale Detail
        saleDetailDTORequest = new SaleDetailDTORequest(2,1L);
        saleDetailDTOResponse = new SaleDetailDTOResponse(1L,BigDecimal.ZERO,2,new BigDecimal("49.90"),productDTOResponse,1L);

        saleDetailDTORequestsList = List.of(saleDetailDTORequest);
        saleDetailDTOResponsesList = List.of(saleDetailDTOResponse);

        // sale
        saleDTORequest = new SaleDTORequest(true,1L,1L,saleDetailDTORequestsList);
        saleDTOResponse = new SaleDTOResponse(1L,new BigDecimal("99.80"),new BigDecimal("17.96"),true,clientDTOResponse,userDTOResponse,saleDetailDTOResponsesList);
        saleDTOResponse2 = new SaleDTOResponse(2L,new BigDecimal("99.88"),new BigDecimal("17.00"),true,clientDTOResponse,userDTOResponse,saleDetailDTOResponsesList);

        saleDTOResponsesList =  List.of(saleDTOResponse,saleDTOResponse2);



    }
    // Contains
    public static final String MESSAGE_NOT_FOUND = "Sale not found";
    public static final String ERROR_REQUIRED = "This field is required";
    public static final String ERROR_POSITIVE = "The value must be positive";


    @Nested
    @DisplayName("GET /sales")
    class GetSalesTest {

        // List
        @Test
        @DisplayName("should return full list of sales")
        public void shouldReturnListOfSales() throws Exception {
            // Arrange
            when(saleService.findAll()).thenReturn(saleDTOResponsesList);
            log.info("sale - " + saleDTOResponsesList.size());
            // Act & Assert
            mockMvc.perform(get(APISALES))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].saleId").value(1L))
                    .andExpect(jsonPath("$[0].saleTotal").value(new  BigDecimal("99.80")))
                    .andExpect(jsonPath("$[1].saleId").value(2L))
                    .andExpect(jsonPath("$[1].saleTotal").value(new BigDecimal("99.88")));

            // Verify
            verify(saleService, times(1)).findAll();
        }

        // List Page
        @Test
        @DisplayName("should return paginated list of sales")
        public void shouldReturnPaginatedListOfSales() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5, Sort.by("saleId").ascending());
            Page<SaleDTOResponse> pageSale = new PageImpl<>(saleDTOResponsesList,pageable,saleDTOResponsesList.size());

            when(saleService.findAllPage(any(Pageable.class))).thenReturn(pageSale);
            // Act & Assert
            mockMvc.perform(get(APISALES+"/page")
                            .param("saleId","0")
                            .param("size","5")
                            .param("sort","saleId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].saleId").value(1L))
                    .andExpect(jsonPath("$.content[0].saleTotal").value(new  BigDecimal("99.80")))
                    .andExpect(jsonPath("$.content[1].saleId").value(2L))
                    .andExpect(jsonPath("$.content[1].saleTotal").value(new BigDecimal("99.88")));

            // Verify
            verify(saleService, times(1)).findAllPage(any(Pageable.class));
        }

        // List Page Empty
        @Test
        @DisplayName("should return empty paginated list of sales")
        public void shouldReturnEmptyPaginatedListOfSales() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5,Sort.by("saleId").ascending());
            Page<SaleDTOResponse> emptyPage = new PageImpl<>(Collections.emptyList(),pageable,0);
            when(saleService.findAllPage(any(Pageable.class))).thenReturn(emptyPage);
            // Act & Assert
            mockMvc.perform(get(APISALES+"/page")
                    .param("saleId","0")
                    .param("size","5")
                    .param("sort","saleId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(0)));
            // Verify
            verify(saleService, times(1)).findAllPage(any(Pageable.class));
        }
        // find by Id Success
        @Test
        @DisplayName("should return sale by id")
        void shouldReturnSaleById() throws Exception {
            // Arrange
            Long saleId = 1L;
            when(saleService.findById(saleId)).thenReturn(saleDTOResponse);

            // Act & Assert
            mockMvc.perform(get(APISALES+"/"+saleId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.saleId").value(saleId))
                    .andExpect(jsonPath("$.saleTotal").value(99.80))
                    .andExpect(jsonPath("$.saleTax").value(17.96))
                    .andExpect(jsonPath("$.saleEnabled").value(true))

                    // Client
                    .andExpect(jsonPath("$.client.clientId").value(1))
                    .andExpect(jsonPath("$.client.clientFirstName").value("Juan"))
                    .andExpect(jsonPath("$.client.clientLastName").value("Pérez"))
                    .andExpect(jsonPath("$.client.clientEmail").value("juan.perez@example.com"))
                    .andExpect(jsonPath("$.client.clientCardId").value("ID12345699"))
                    .andExpect(jsonPath("$.client.clientPhone").value("987654321"))
                    .andExpect(jsonPath("$.client.clientAddress").value("Av. Siempre Viva 742"))

                    // User
                    .andExpect(jsonPath("$.user.userId").value(1))
                    .andExpect(jsonPath("$.user.userName").value("Alexis"))
                    .andExpect(jsonPath("$.user.userEnabled").value(true))

                    // Role (inside user)
                    .andExpect(jsonPath("$.user.role.roleId").value(1))
                    .andExpect(jsonPath("$.user.role.roleName").value("ADMIN"))
                    .andExpect(jsonPath("$.user.role.roleEnabled").value(true))

                    // SaleDetails (list)
                    .andExpect(jsonPath("$.details[0].saleDetailId").value(1))
                    .andExpect(jsonPath("$.details[0].discount").value(0))
                    .andExpect(jsonPath("$.details[0].quantity").value(2))
                    .andExpect(jsonPath("$.details[0].salePrice").value(49.90))
                    .andExpect(jsonPath("$.details[0].saleId").value(1))

                    // Product (inside saleDetail)
                    .andExpect(jsonPath("$.details[0].product.productId").value(1))
                    .andExpect(jsonPath("$.details[0].product.productName").value("CamisasTep"))
                    .andExpect(jsonPath("$.details[0].product.productDescription").value("Camisas de la marca Tep 2025."))
                    .andExpect(jsonPath("$.details[0].product.productPrice").value(49.90))
                    .andExpect(jsonPath("$.details[0].product.productEnabled").value(true))

                    // Category (inside product)
                    .andExpect(jsonPath("$.details[0].product.category.categoryId").value(1))
                    .andExpect(jsonPath("$.details[0].product.category.categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.details[0].product.category.categoryDescription").value("Categoría de ropa"))
                    .andExpect(jsonPath("$.details[0].product.category.categoryEnabled").value(true));

            // Verify
            verify(saleService, times(1)).findById(saleId);
        }

        //find by id Fail 404
        @Test
        @DisplayName("should return 404 when sale ID does not exist")
        public void shouldReturn404WhenSaleIDDoesNotExist() throws Exception {
            // Arrange
            Long saleIdNonExisted = 99L;
            when(saleService.findById(saleIdNonExisted)).thenThrow(new ResourceNotFoundException(MESSAGE_NOT_FOUND));

            // Act & Assert
            mockMvc.perform(get(APISALES+"/"+saleIdNonExisted))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message", is(MESSAGE_NOT_FOUND)))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.path").value(APISALES+"/"+saleIdNonExisted))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(saleService, times(1)).findById(saleIdNonExisted);
        }




    }


}
