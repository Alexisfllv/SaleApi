package sora.com.saleapi.serviceimplTest.SaveSale;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("SaleHelperService Test")
public class SaleHelperServiceTest {

    @Mock private ClientRepo clientRepo;
    @Mock private UserRepo userRepo;
    @Mock private ProductRepo productRepo;

    @InjectMocks
    private SaleHelperService saleHelperService;

    private Client client;
    private User user;
    private Product product;
    private Sale sale;
    private SaleDetailDTORequest detailDTO;

    @BeforeEach
    void setUp() {
        client = new Client(1L, "John", "Doe", "john@example.com", "12345678", "987654321", "LIMA");
        user = new User(1L, "admin", "secret", true, new Role(1L, "ADMIN", true));
        product = new Product(1L, "Laptop", "Gaming laptop", new BigDecimal("1500.00"), true,
                new Category(1L, "Tec", "Desc...", true));
        sale = new Sale();
        detailDTO = new SaleDetailDTORequest(2, 1L);
    }

    @Nested
    @DisplayName("validateRequest()")
    class ValidateRequestTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when request is null")
        void shouldThrowWhenRequestIsNull() {
            SaleDTORequest request = null;
            assertThrows(IllegalArgumentException.class, () -> saleHelperService.validateRequest(request));
        }

        @Test
        @DisplayName("Should throw Sale.ClientId is null")
        void shouldThrowWhenrRequestClientIdIsNull() {
            SaleDTORequest requestClientNull = new SaleDTORequest(true,null,1L,List.of(detailDTO));
            assertThrows(IllegalArgumentException.class, () -> saleHelperService.validateRequest(requestClientNull));
        }

        @Test
        @DisplayName("Should throw Sale.UserId is null")
        void shouldThrowWhenRequestUserIdIsNull() {
            SaleDTORequest requestUserNull = new SaleDTORequest(true,1L,null,List.of(detailDTO));
            assertThrows(IllegalArgumentException.class, () -> saleHelperService.validateRequest(requestUserNull));
        }

        @Test
        @DisplayName("Should throw when details is null")
        void shouldThrowWhenRequestDetailsIsNull() {
            SaleDTORequest requestDetailNull = new SaleDTORequest(true,1L,1L,null);
            assertThrows(IllegalArgumentException.class, () -> saleHelperService.validateRequest(requestDetailNull));
        }
        @Test
        @DisplayName("Should throw when details is empty")
        void shouldThrowWhenRequestDetailsIsEmpty() {
            SaleDTORequest requestDetailEmpty = new SaleDTORequest(true,1L,1L,List.of());
            assertThrows(IllegalArgumentException.class, () -> saleHelperService.validateRequest(requestDetailEmpty));
        }

        @Test
        @DisplayName("Should pass with valid request")
        void shouldPassWithValidRequest() {
            SaleDTORequest request = new SaleDTORequest(true, 1L, 1L, List.of(detailDTO));
            assertDoesNotThrow(() -> saleHelperService.validateRequest(request));
        }
    }

    @Nested
    @DisplayName("findClient()")
    class FindClientTests {

        @Test
        @DisplayName("Should return client when ID exists")
        void shouldReturnClient() {
            when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
            Client result = saleHelperService.findClient(1L);
            assertEquals(client, result);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void shouldThrowWhenClientNotFound() {
            when(clientRepo.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> saleHelperService.findClient(99L));
        }
    }

    @Nested
    @DisplayName("findUser()")
    class FindUserTests {

        @Test
        @DisplayName("Should return user when ID exists")
        void shouldReturnUser() {
            when(userRepo.findById(1L)).thenReturn(Optional.of(user));
            User result = saleHelperService.findUser(1L);
            assertEquals(user, result);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userRepo.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> saleHelperService.findUser(99L));
        }
    }

    @Nested
    @DisplayName("buildDetails()")
    class BuildDetailsTests {

        @Test
        @DisplayName("Should return SaleDetail list when request is valid")
        void shouldReturnSaleDetails() {
            when(productRepo.findById(1L)).thenReturn(Optional.of(product));
            List<SaleDetail> result = saleHelperService.buildDetails(sale, List.of(detailDTO));
            SaleDetail detail = result.get(0);

            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(product, detail.getProduct()),
                    () -> assertEquals(2, detail.getQuantity()),
                    () -> assertEquals(new BigDecimal("1500.00"), detail.getSalePrice()),
                    () -> assertEquals(BigDecimal.ZERO, detail.getDiscount()),
                    () -> assertEquals(sale, detail.getSale())
            );
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowWhenProductNotFound() {
            when(productRepo.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> saleHelperService.buildDetails(sale, List.of(detailDTO)));
        }
    }

    @Nested
    @DisplayName("calculateTotal()")
    class CalculateTotalTests {

        @Test
        @DisplayName("Should return correct total when details are provided")
        void shouldReturnTotal() {
            SaleDetail d1 = new SaleDetail(null, BigDecimal.ZERO, 2, new BigDecimal("100.00"), product, sale);
            SaleDetail d2 = new SaleDetail(null, BigDecimal.ZERO, 1, new BigDecimal("200.00"), product, sale);
            List<SaleDetail> listDetail = List.of(d1, d2);

            BigDecimal total = saleHelperService.calculateTotal(listDetail);
            assertEquals(new BigDecimal("400.00"), total);
        }

        @Test
        @DisplayName("Should return zero when detail list is empty")
        void shouldReturnZero() {
            BigDecimal total = saleHelperService.calculateTotal(List.of());
            assertEquals(BigDecimal.ZERO, total);
        }
    }
}
