package sora.com.saleapi.controllerTest;




// static
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import sora.com.saleapi.service.SaleService;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

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
        saleDetailDTOResponse = new SaleDetailDTOResponse(1L,BigDecimal.ZERO,2,new BigDecimal("49.90"),productDTOResponse,2L);

        saleDetailDTORequestsList = List.of(saleDetailDTORequest);
        saleDetailDTOResponsesList = List.of(saleDetailDTOResponse);

        // sale
        saleDTORequest = new SaleDTORequest(true,1L,1L,saleDetailDTORequestsList);
        saleDTOResponse = new SaleDTOResponse(2L,new BigDecimal("99.80"),new BigDecimal("17.96"),true,clientDTOResponse,userDTOResponse,saleDetailDTOResponsesList);
        saleDTOResponse2 = new SaleDTOResponse(2L,new BigDecimal("99.80"),new BigDecimal("17.96"),true,clientDTOResponse,userDTOResponse,saleDetailDTOResponsesList);

        saleDTOResponsesList =  List.of(saleDTOResponse,saleDTOResponse2);

    }

}
