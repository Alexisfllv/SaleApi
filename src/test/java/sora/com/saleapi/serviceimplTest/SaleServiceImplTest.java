package sora.com.saleapi.serviceimplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;
import sora.com.saleapi.entity.*;
import sora.com.saleapi.mapper.SaleMapper;
import sora.com.saleapi.repo.SaleRepo;
import sora.com.saleapi.service.Impl.SaleServiceImpl;
import sora.com.saleapi.service.Impl.SaveSale.SaleHelperService;
// static
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
@ExtendWith(SpringExtension.class)
public class SaleServiceImplTest {

    @Mock
    private SaleHelperService saleHelperService;

    @Mock
    private SaleMapper saleMapper;

    @Mock
    private SaleRepo saleRepo;

    @InjectMocks
    private SaleServiceImpl saleService;

    // Objts
    private SaleDTORequest saleDTORequest;
    private Sale saleMapped;
    private Client client;
    private User user;
    private SaleDetailDTORequest detailDTO;
    private List<SaleDetail> saleDetailList;
    private SaleDTOResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // DTO detalle
        detailDTO = new SaleDetailDTORequest(2,1L);

        // DTO principal
        saleDTORequest = new SaleDTORequest(true,1L,1L,List.of(detailDTO));

        // Entity
        client = new Client(1L, "John", "Doe", "john@example.com", "12345678", "987654321", "LIMA");
        Role role = new Role(1L, "ADMIN", true);
        user = new User(1L, "admin", "secret", true, role);

        // Venta mapeada desde DTO
        saleMapped = new Sale();
        saleMapped.setSaleEnabled(true);

        // Detalles construidos por helper
        SaleDetail detail = new SaleDetail(null, BigDecimal.ZERO, 2, new BigDecimal("100.00"), null, saleMapped);
        saleDetailList = List.of(detail);

        // DTO de respuesta Esperado
        expectedResponse = new SaleDTOResponse(1L, new BigDecimal("200.00"), new BigDecimal("36.00"), true, null, null, List.of());

    }

    @Test
    void givenValidSaleDTORequest_whenSave_thenReturnSaleDTOResponse(){
        // Arrange
        when(saleMapper.toSale(saleDTORequest)).thenReturn(saleMapped);

            // Validar la request
        doNothing().when(saleHelperService).validateRequest(saleDTORequest);
            // Validar el Id Client
        when(saleHelperService.findClient(1L)).thenReturn(client);
            // Validar el Id User
        when(saleHelperService.findUser(1L)).thenReturn(user);
            // Armar el SaleDetail
        when(saleHelperService.buildDetails(saleMapped,List.of(detailDTO))).thenReturn(saleDetailList);
            // Calcular el total del SaleDetail
        when(saleHelperService.calculateTotal(saleDetailList)).thenReturn(new BigDecimal("200.00"));

        when(saleRepo.save(saleMapped)).thenReturn(any(Sale.class));
        when(saleMapper.toSaleDTOResponse(saleMapped)).thenReturn(expectedResponse);

        // Act
        SaleDTOResponse result = saleService.save(saleDTORequest);
        // Assert & Verify

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse.saleId(), result.saleId()),
                () -> assertEquals(expectedResponse.saleTotal(), result.saleTotal()),
                () -> assertEquals(expectedResponse.saleTax(), result.saleTax())
        );
        // Verify
        verify(saleHelperService, times(1)).validateRequest(saleDTORequest);
        verify(saleMapper, times(1)).toSale(saleDTORequest);
        verify(saleHelperService, times(1)).findClient(1L);
        verify(saleHelperService, times(1)).findUser(1L);
        verify(saleHelperService, times(1)).buildDetails(saleMapped,List.of(detailDTO));
        verify(saleHelperService, times(1)).calculateTotal(saleDetailList);
        verify(saleRepo, times(1)).save(saleMapped);
        verify(saleMapper, times(1)).toSaleDTOResponse(saleMapped);
        verifyNoMoreInteractions(saleHelperService,saleRepo,saleMapper);
    }

}
