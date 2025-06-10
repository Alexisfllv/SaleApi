package sora.com.saleapi.serviceimplTest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.entity.Category;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.CategoryMapper;
import sora.com.saleapi.repo.CategoryRepo;
import sora.com.saleapi.service.Impl.CategoryServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// static
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {


    //Mock de category repo
    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    // valores comunes reutilizables
    private Category category1;
    private Category category2;
    private Category category3;

    private CategoryDTOResponse categoryDTOResponse1;
    private CategoryDTOResponse categoryDTOResponse2;
    private CategoryDTOResponse categoryDTOResponse3;

    private CategoryDTORequest categoryDTORequest1;
    private Category categoryRequest1;

    // asignar valores
    @BeforeEach
    public void setUp() {
        // inicio
        category1 = new Category(1L,"Ropa","Ropa americana y Europea.", true);
        category2 = new Category(2L, "Tecnología", "Dispositivos electrónicos y accesorios.", true);
        category3 = new Category(3L, "Hogar", "Productos para el hogar y decoración.", true);

        categoryDTOResponse1 = new CategoryDTOResponse(1L,"Ropa","Ropa americana y Europea.", true);
        categoryDTOResponse2 = new CategoryDTOResponse(2L, "Tecnología", "Dispositivos electrónicos y accesorios.", true);
        categoryDTOResponse3 = new CategoryDTOResponse(3L, "Hogar", "Productos para el hogar y decoración.", true);

        categoryDTORequest1 = new CategoryDTORequest("Ropa","Ropa americana y Europea.", true);
        categoryRequest1 = new Category(null,"Ropa","Ropa americana y Europea.", true);

    }

    // test listado
    @Test
    void givenCategoriesExist_whenListAll_thenReturnsAllCategories() {
        //Arrange
        List<Category> categories = List.of(category1,category2,category3);
        when(categoryRepo.findAll()).thenReturn(categories);

        List<CategoryDTOResponse> categoriesResponseDTO = List.of(categoryDTOResponse1,categoryDTOResponse2,categoryDTOResponse3);

            // Simulacion de Mapeo
        when(categoryMapper.toCategoryDTOResponse(category1)).thenReturn(categoryDTOResponse1);
        when(categoryMapper.toCategoryDTOResponse(category2)).thenReturn(categoryDTOResponse2);
        when(categoryMapper.toCategoryDTOResponse(category3)).thenReturn(categoryDTOResponse3);

        //Act
        List<CategoryDTOResponse> result = categoryService.findAll();

        //Acert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.size()),
                () -> assertEquals(categoryDTOResponse1,result.get(0)),
                () -> assertEquals(categoryDTOResponse2,result.get(1)),
                () -> assertEquals(categoryDTOResponse3,result.get(2))
        );

        verify(categoryRepo).findAll();
        verify(categoryMapper).toCategoryDTOResponse(category1);
        verify(categoryMapper).toCategoryDTOResponse(category2);
        verify(categoryMapper).toCategoryDTOResponse(category3);
        verifyNoMoreInteractions(categoryRepo,categoryMapper);
    }

    // test de listado vacio
    @Test
    void givenCategoriesDoesNotExist_whenListAll_thenReturnsEmptyList() {
        //Arrange
        List<Category> categories = List.of();
        when(categoryRepo.findAll()).thenReturn(categories);

        //Act
        List<CategoryDTOResponse> result = categoryService.findAll();

        //Asert & Verify

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
        verify( categoryRepo).findAll();
    }

    // test de busqueda exitoso de category
    @Test
    void givenExistingCategoryId_whenFindById_thenReturnsCategoryDTO() {
        // Arrange
        Long id = 1L;
        Category categoryExist = category1;

        when(categoryRepo.findById(id)).thenReturn(Optional.of(categoryExist));
        CategoryDTOResponse categoryDTOResponseExist = categoryDTOResponse1;
        when(categoryMapper.toCategoryDTOResponse(categoryExist)).thenReturn(categoryDTOResponseExist);

        // Act
        CategoryDTOResponse result = categoryService.findById(id);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(id, result.categoryId()),
                () -> assertEquals("Ropa", result.categoryName()),
                () -> assertEquals("Ropa americana y Europea.", result.categoryDescription()),
                () -> assertEquals(true, result.categoryEnabled())
        );
        verify(categoryRepo).findById(id);
        verify(categoryMapper).toCategoryDTOResponse(categoryExist);
        verifyNoMoreInteractions(categoryRepo,categoryMapper);
    }

    // buscar un id de category que no existe.
    @Test
    void givenNonExistingCategoryId_whenFindById_thenThrowsResourceNotFoundException() {

        // Arrange
        Long id = 99L;
        when(categoryRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(id));

        // Verify
        verify(categoryRepo).findById(id);
        verifyNoMoreInteractions(categoryRepo,categoryMapper);
    }

    // test de registrar una category
    @Test
    void givenValidCategoryDTO_whenRegisterCategory_thenReturnsSavedCategory(){
        // Arrange
            // valores con id en null
        CategoryDTORequest categoryDtoRequest =  categoryDTORequest1;
        Category categoryRequest = categoryRequest1;

        when(categoryMapper.toCategory(categoryDtoRequest)).thenReturn(categoryRequest);
        when(categoryRepo.save(categoryRequest)).thenReturn(category1);
        when(categoryMapper.toCategoryDTOResponse(any(Category.class))).thenReturn(categoryDTOResponse1);


        // Act
        CategoryDTOResponse result = categoryService.save(categoryDtoRequest);

        // Assert & Verify
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.categoryId()),
                () -> assertEquals("Ropa", result.categoryName()),
                () -> assertEquals("Ropa americana y Europea.", result.categoryDescription()),
                () -> assertTrue(result.categoryEnabled())
        );

        verify(categoryMapper).toCategory(categoryDtoRequest);
        verify(categoryRepo).save(categoryRequest);
        verify(categoryMapper).toCategoryDTOResponse(any(Category.class));
        verifyNoMoreInteractions(categoryRepo,categoryMapper);
    }


}
