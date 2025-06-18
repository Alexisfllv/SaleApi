package sora.com.saleapi.serviceimplTest.SaveSale;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;
import sora.com.saleapi.entity.*;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.repo.ClientRepo;
import sora.com.saleapi.repo.ProductRepo;
import sora.com.saleapi.repo.UserRepo;
import sora.com.saleapi.service.Impl.SaveSale.SaleHelperService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


// static
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Slf4j
@ExtendWith(SpringExtension.class)
public class SaleHelperServiceTest {

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private SaleHelperService saleHelperService;

    // Valores comunes
    private Client client;
    private User user;
    private Product product;
    private Sale sale;
    private SaleDetailDTORequest detailDTO;

    @BeforeEach
    void setUp() {
        client = new Client(1L, "John", "Doe", "john@example.com", "12345678", "987654321", "LIMA");
        user = new User(1L, "admin", "secret", true, new Role(1L, "ADMIN", true));
        product = new Product(1L, "Laptop", "Gaming laptop", new BigDecimal("1500.00"), true, new Category(1L,"Tec","Desc...",true));
        sale = new Sale(); // vacío para testear buildDetails
        detailDTO = new SaleDetailDTORequest(2, 1L); // 2 unidades de producto con ID 1
    }

    // validate Request
    @Test
    void givenNullRequest_whenValidateRequest_thenThrowException() {
        // Arrange
        SaleDTORequest request = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> saleHelperService.validateRequest(request));
    }

    @Test
    void givenValidRequest_whenValidateRequest_thenNoExceptionThrown(){
        // Arrange
        SaleDTORequest request = new SaleDTORequest(true,1L,1L, List.of(detailDTO));

        // Act & Assert
        assertDoesNotThrow(() -> saleHelperService.validateRequest(request));
    }

    // find client
    @Test
    void givenExistingClientId_whenFindClient_thenReturnClient(){
        // Arrange
        Long idClient = 1L;
        when(clientRepo.findById(idClient)).thenReturn(Optional.of(client));

        // Act
        Client result =  saleHelperService.findClient(idClient);

        // Assert & Verify
        assertEquals(client,result);
    }

    @Test
    void givenNonExistingClientId_whenFindClient_thenThrowException(){
        // Arrange
        Long idClientNonExist = 99L;
        when(clientRepo.findById(idClientNonExist)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> saleHelperService.findClient(idClientNonExist));
    }

    // find user
    @Test
    void givenExistingUserId_whenFindUser_thenReturnUser(){
        // Arrange
        Long idUser = 1L;
        when(userRepo.findById(idUser)).thenReturn(Optional.of(user));
        //Act
        User result = saleHelperService.findUser(idUser);

        // Assert & Verify
        assertEquals(user,result);
    }

    @Test
    void givenNonExistingUserId_whenFindUser_thenThrowException(){
        // Arrange
        Long idUserNonExist = 99L;
        when(userRepo.findById(idUserNonExist)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> saleHelperService.findUser(idUserNonExist));
    }

    // builDetails
    @Test
    void givenValidDetailRequest_whenBuildDetails_thenReturnSaleDetailList(){
        // Arrange
        Long idProduct = 1L;
        when(productRepo.findById(idProduct)).thenReturn(Optional.of(product));
        // Act
        List<SaleDetail> result = saleHelperService.buildDetails(sale,List.of(detailDTO));

        // Assert & Verify
        SaleDetail detail = result.get(0);
        log.warn(detail.getSale() + " sale");
        assertAll(
                () -> assertEquals(1,result.size()),
                () -> assertEquals(product,detail.getProduct()),
                () -> assertEquals(2,detail.getQuantity()),
                () -> assertEquals(new BigDecimal("1500.00"),detail.getSalePrice()),
                () -> assertEquals(BigDecimal.ZERO,detail.getDiscount()),
                () -> assertEquals(sale,detail.getSale())
        );
    }

    // noProductId
    @Test
    void givenNonExistingProduct_whenBuildDetails_thenThrowException (){
        // Arrange
        Long idProductNonExist = 99L;
        when(productRepo.findById(idProductNonExist)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> saleHelperService.buildDetails(sale,List.of(detailDTO)));
    }

    // Calculate Total
    @Test
    void givenSaleDetails_whenCalculateTotal_thenReturnTotal(){

        // Arrange
        SaleDetail d1 = new SaleDetail(null, BigDecimal.ZERO, 2, new BigDecimal("100.00"), product, sale);
        SaleDetail d2 = new SaleDetail(null, BigDecimal.ZERO, 1, new BigDecimal("200.00"), product, sale);
        List<SaleDetail> listDetail = List.of(d1, d2);
        // Act
        BigDecimal total = saleHelperService.calculateTotal(listDetail);

        // Assert & Verify
        assertAll(
                () -> assertEquals(new BigDecimal("400.00"),total)
        );
    }

    @Test
    void givenEmptyDetails_whenCalculateTotal_thenReturnZero(){
        // Arrange
        List<SaleDetail> listDetail = List.of();
        // Act
        BigDecimal total = saleHelperService.calculateTotal(listDetail);
        // Assert & Verify
        assertEquals(BigDecimal.ZERO,total);
    }




}
