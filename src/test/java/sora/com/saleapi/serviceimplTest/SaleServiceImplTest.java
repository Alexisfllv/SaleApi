package sora.com.saleapi.serviceimplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.dto.ProductDTO.ProductDTOResponse;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTOResponse;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.entity.*;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.SaleMapper;
import sora.com.saleapi.repo.SaleRepo;
import sora.com.saleapi.service.Impl.SaleServiceImpl;
import sora.com.saleapi.service.Impl.SaveSale.SaleHelperService;
// static
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    private Product product;
    private SaleDetail saleDetail;
    private ClientDTOResponse clientDTOResponse;
    private UserDTOResponse userDTOResponse;
    private CategoryDTOResponse categoryDTOResponse;
    private ProductDTOResponse productDTOResponse;
    private SaleDTOResponse expectedResponse;
    private SaleDetailDTORequest detailDTO;
    private List<SaleDetail> saleDetails;

    @BeforeEach
    void setUp() {
        // --- Preparar DTO de entrada ---
        detailDTO = new SaleDetailDTORequest(2, 1L);
        saleDTORequest = new SaleDTORequest(true, 1L, 1L, List.of(detailDTO));

        // valores de la entidad sale
        client = new Client(1L, "John", "Doe", "john@example.com", "12345678", "987654321", "LIMA");
        Role role = new Role(1L, "ADMIN", true);
        user = new User(1L, "admin", "secret", true, role);
        Category category = new Category(1L, "Electrónica", "Dispositivos", true);
        product = new Product(1L, "Laptop", "Gaming", new BigDecimal("100.00"), true, category);

        // detalle de venta entidad
        saleDetail = new SaleDetail(null, BigDecimal.ZERO, 2, product.getProductPrice(), product, null);
        saleDetails = List.of(saleDetail);

        // contruccion de sale
        saleMapped = new Sale();
        saleMapped.setSaleId(1L);
        saleMapped.setSaleEnabled(true);
        saleMapped.setClient(client);
        saleMapped.setUser(user);
        // detalles de venta
        saleMapped.setDetails(saleDetails);
        saleMapped.setSaleTotal(new BigDecimal("200.00"));
        saleMapped.setSaleTax(new BigDecimal("36.00"));
        saleDetail.setSale(saleMapped); // setear sale en detail

        // ---------- DTOs de respuesta esperados ----------
        clientDTOResponse = new ClientDTOResponse(1L, "John", "Doe", "john@example.com", "12345678", "987654321", "LIMA");
        RoleDTOResponse roleDTO = new RoleDTOResponse(1L, "ADMIN", true);
        userDTOResponse = new UserDTOResponse(1L, "admin", true, roleDTO);
        categoryDTOResponse = new CategoryDTOResponse(1L, "Electrónica", "Dispositivos", true);
        productDTOResponse = new ProductDTOResponse(1L, "Laptop", "Gaming", new BigDecimal("100.00"), true, categoryDTOResponse);
        SaleDetailDTOResponse detailResponse = new SaleDetailDTOResponse(null, BigDecimal.ZERO, 2, new BigDecimal("100.00"), productDTOResponse, 1L);

        // creando la salida SALEDTORESPONSE
        expectedResponse = new SaleDTOResponse(
                1L,
                new BigDecimal("200.00"),
                new BigDecimal("36.00"),
                true,
                clientDTOResponse,
                userDTOResponse,
                List.of(detailResponse)
        );


    }

    // copiar sale entity
    private Sale copySale(Sale original, Long newId) {
        Sale copy = new Sale();
        copy.setSaleId(newId);
        copy.setSaleEnabled(original.getSaleEnabled());
        copy.setClient(original.getClient());
        copy.setUser(original.getUser());
        copy.setSaleTotal(original.getSaleTotal());
        copy.setSaleTax(original.getSaleTax());

        // Copiar detalles y vincularlos al nuevo sale
        List<SaleDetail> copiedDetails = original.getDetails().stream()
                .map(d -> {
                    SaleDetail detail = new SaleDetail();
                    detail.setQuantity(d.getQuantity());
                    detail.setSalePrice(d.getSalePrice());
                    detail.setDiscount(d.getDiscount());
                    detail.setProduct(d.getProduct());
                    detail.setSale(copy); // asignar al nuevo sale
                    return detail;
                })
                .toList();

        copy.setDetails(copiedDetails);
        return copy;
    }

    // duplicar dto
    private SaleDTOResponse cloneSaleDTOResponseWithId(SaleDTOResponse base, Long newId) {
        return new SaleDTOResponse(
                newId,
                base.saleTotal(),
                base.saleTax(),
                base.saleEnabled(),
                base.client(),
                base.user(),
                base.details()
        );
    }



    @Test
    void givenValidSaleDTORequest_whenSave_thenReturnSaleDTOResponse(){

        // Arrange
            // validar saleDtoRequest
        doNothing().when(saleHelperService).validateRequest(saleDTORequest);
            // mapear dtoreq a entidad sale
        when(saleMapper.toSale(saleDTORequest)).thenReturn(saleMapped);
            // validar id client
        when(saleHelperService.findClient(saleDTORequest.clientId())).thenReturn(client);
            // validar id user
        when(saleHelperService.findUser(saleDTORequest.userId())).thenReturn(user);
            // construir el detail
        when(saleHelperService.buildDetails(saleMapped,List.of(detailDTO))).thenReturn(List.of(saleDetail));
            // construir el total
        when(saleHelperService.calculateTotal(saleDetails)).thenReturn(new BigDecimal("200.00"));
            // guardar el sale
        when(saleRepo.save(saleMapped)).thenReturn(saleMapped);
            // convertir a DTOResponse
        when(saleMapper.toSaleDTOResponse(saleMapped)).thenReturn(expectedResponse);

        // Act
        SaleDTOResponse result = saleService.save(saleDTORequest);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.saleId()),
                () -> assertEquals(new BigDecimal("200.00"), result.saleTotal()),
                () -> assertEquals(new BigDecimal("36.00"), result.saleTax()),
                () -> assertTrue(result.saleEnabled()),
                () -> assertEquals("John", result.client().clientFirstName()),
                () -> assertEquals("admin", result.user().userName()),
                () -> assertEquals("Laptop", result.details().get(0).product().productName()),
                () -> assertEquals(2, result.details().get(0).quantity()),
                () -> assertEquals(new BigDecimal("100.00"), result.details().get(0).salePrice())
        );
        verify(saleHelperService, times(1)).validateRequest(saleDTORequest);
        verify(saleMapper, times(1)).toSale(saleDTORequest);
        verify(saleHelperService,times(1)).findClient(1L);
        verify(saleHelperService,times(1)).findUser(1L);
        verify(saleHelperService,times(1)).buildDetails(saleMapped,List.of(detailDTO));
        verify(saleHelperService,times(1)).calculateTotal(saleDetails);
        verify(saleRepo,times(1)).save(saleMapped);
        verify(saleMapper,times(1)).toSaleDTOResponse(saleMapped);
        verifyNoMoreInteractions(saleHelperService,saleMapper,saleRepo);
    }

    // test de listado de sale
    @Test
    void givenSalesExist_whenFindAll_thenReturnListOfSaleDTOs(){
        // Arrange
        List<Sale> saleList = List.of(
                saleMapped,
                copySale(saleMapped, 2L),
                copySale(saleMapped, 3L)
        );
        SaleDTOResponse resdto1 = expectedResponse;
        SaleDTOResponse resdto2 = cloneSaleDTOResponseWithId(expectedResponse, 2L);
        SaleDTOResponse resdto3 = cloneSaleDTOResponseWithId(expectedResponse, 3L);

        when(saleRepo.findAll()).thenReturn(saleList);
        when(saleMapper.toSaleDTOResponse(saleList.get(0))).thenReturn(resdto1);
        when(saleMapper.toSaleDTOResponse(saleList.get(1))).thenReturn(resdto2);
        when(saleMapper.toSaleDTOResponse(saleList.get(2))).thenReturn(resdto3);

        // Act
        List<SaleDTOResponse> result = saleService.findAll();
        // Assert & Verfiy
        assertAll(
                () -> assertEquals(3,result.size()),
                () -> assertEquals(resdto1,result.get(0)),
                () -> assertEquals(resdto2,result.get(1)),
                () -> assertEquals(resdto3,result.get(2))
        );
        verify(saleRepo,times(1)).findAll();
        verify(saleMapper,times(1)).toSaleDTOResponse(saleList.get(0));
        verify(saleMapper,times(1)).toSaleDTOResponse(saleList.get(1));
        verify(saleMapper,times(1)).toSaleDTOResponse(saleList.get(2));
        verifyNoMoreInteractions(saleRepo,saleMapper);
    }

    // list sale pageable
    @Test
    void givenSalesExistInPage_whenFindAllPage_thenReturnPageOfSaleDTOs(){
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Sale> saleList = List.of(saleMapped,copySale(saleMapped, 2L),copySale(saleMapped, 3L));
        Page<Sale> page = new PageImpl<>(saleList, pageable, saleList.size());
        SaleDTOResponse resdto1 = expectedResponse;
        SaleDTOResponse resdto2 = cloneSaleDTOResponseWithId(expectedResponse, 2L);
        SaleDTOResponse resdto3 = cloneSaleDTOResponseWithId(expectedResponse, 3L);
        when(saleRepo.findAll(pageable)).thenReturn(page);
        when(saleMapper.toSaleDTOResponse(saleList.get(0))).thenReturn(resdto1);
        when(saleMapper.toSaleDTOResponse(saleList.get(1))).thenReturn(resdto2);
        when(saleMapper.toSaleDTOResponse(saleList.get(2))).thenReturn(resdto3);
        // Act
        Page<SaleDTOResponse> result = saleService.findAllPage(pageable);
        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3,result.getContent().size()),
                () -> assertEquals(resdto1,result.getContent().get(0)),
                () -> assertEquals(resdto2,result.getContent().get(1)),
                () -> assertEquals(resdto3,result.getContent().get(2))
        );
        verify(saleRepo,times(1)).findAll(pageable);
        verify(saleMapper,times(1)).toSaleDTOResponse(saleList.get(0));
        verify(saleMapper,times(1)).toSaleDTOResponse(saleList.get(1));
        verify(saleMapper,times(1)).toSaleDTOResponse(saleList.get(2));
    }

    // lista paginado vacio
    @Test
    void givenNoSales_whenFindAllPage_thenReturnEmptyPage(){
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Sale> saleList = List.of();
        Page<Sale> page = new PageImpl<>(saleList, pageable, saleList.size());
        when(saleRepo.findAll(pageable)).thenReturn(page);
        // Act
        Page<SaleDTOResponse> result = saleService.findAllPage(pageable);

        // Assert & Verify
        assertAll(
                () -> assertEquals(0,result.getContent().size())
        );
        verify(saleRepo,times(1)).findAll(pageable);
        verifyNoMoreInteractions(saleRepo);
    }

    // buscar sale por id
    @Test
    void givenSaleExists_whenFindById_thenReturnSaleDTO(){
        // Arrange
        Long idSaleExist = 1L;
        Sale saleExist = saleMapped;
        SaleDTOResponse saleDTOResponse = expectedResponse;
        when(saleRepo.findById(idSaleExist)).thenReturn(Optional.of(saleExist));
        when(saleMapper.toSaleDTOResponse(saleExist)).thenReturn(saleDTOResponse);
        // Act
        SaleDTOResponse result = saleService.findById(idSaleExist);

        // Assert & Verfiy
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(saleDTOResponse,result)
        );
        verify(saleRepo,times(1)).findById(idSaleExist);
        verify(saleMapper,times(1)).toSaleDTOResponse(saleExist);
        verifyNoMoreInteractions(saleRepo,saleMapper);
    }

    // buscar sale por id Throw
    @Test
    void givenSaleDoesNotExist_whenFindById_thenThrowResourceNotFoundException(){
        // Arrange
        Long idSaleNonExist = 99L;
        when(saleRepo.findById(idSaleNonExist)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> saleService.findById(idSaleNonExist));
        // Verify
        verify(saleRepo,times(1)).findById(idSaleNonExist);
        verifyNoMoreInteractions(saleRepo);
    }

    // eliminar sale exitoso
    @Test
    void givenSaleExists_whenDeleteById_thenDeleteSuccessfully(){
        // Arrange
        Long idSaleExist = 1L;
        Sale saleExist = saleMapped;
        when(saleRepo.findById(idSaleExist)).thenReturn(Optional.of(saleExist));
        // Act
        saleService.deleteById(idSaleExist);
        // Assert & Verify
        verify(saleRepo,times(1)).findById(idSaleExist);
        verify(saleRepo,times(1)).delete(saleExist);
        verifyNoMoreInteractions(saleRepo);
    }

    // eliminar sale fail
    @Test
    void givenSaleDoesNotExist_whenDeleteById_thenThrowResourceNotFoundException(){
        // Arrange
        Long idSaleNonExist = 99L;
        when(saleRepo.findById(idSaleNonExist)).thenReturn(Optional.empty());
        // Act
        assertThrows(ResourceNotFoundException.class, () -> saleService.deleteById(idSaleNonExist));
        // Assert & Verify
        verify(saleRepo,times(1)).findById(idSaleNonExist);
        verifyNoMoreInteractions(saleRepo);
    }






}
