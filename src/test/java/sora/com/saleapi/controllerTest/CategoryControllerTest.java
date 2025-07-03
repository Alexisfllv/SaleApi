package sora.com.saleapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sora.com.saleapi.controller.CategoryController;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTORequest;
import sora.com.saleapi.dto.CategoryDTO.CategoryDTOResponse;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.CategoryMapper;
import sora.com.saleapi.service.CategoryService;
// static
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    // var
    private CategoryDTORequest categoryDTORequest;

    private CategoryDTOResponse categoryDTOResponse;
    private CategoryDTOResponse categoryDTOResponse2;
    private List<CategoryDTOResponse> categoryDTOResponseList;

    private String APICATEGORY = "/api/v1/categories";

    @BeforeEach
    public void setup() {
        categoryDTORequest = new CategoryDTORequest("Ropa", "Categoría de ropa", true);

        categoryDTOResponse = new CategoryDTOResponse(1L, "Ropa", "Categoría de ropa", true);
        categoryDTOResponse2 = new CategoryDTOResponse(2L, "Tecnología", "Categoría tecnológica", true);
        categoryDTOResponseList = List.of(categoryDTOResponse,categoryDTOResponse2);
    }

    public class TestConstants {
        public static final String MESSAGE_CREATED = "Creado correctamente";
        public static final String MESSAGE_UPDATED = "Actualizado correctamente";
        public static final String MESSAGE_DELETED = "Eliminado correctamente";
        public static final String MESSAGE_NOT_FOUND = "Category not found";
        public static final String ERROR_REQUIRED = "This field is required";
        public static final String ERROR_INVALID_FORMAT = "Invalid format";
        public static final String ERROR_SIZE_NAME = "The number of items must be between 2 and 50";
        public static final String ERROR_SIZE_DESCRIPTION = "The number of items must be between 2 and 250";
    }


    @Nested
    class GetCategoryTests {

        // test de listar
        @Test
        public void givenCategoriesExist_whenGetAll_thenReturnCategoryList() throws Exception {
            // Arrange
            when(categoryService.findAll()).thenReturn(categoryDTOResponseList);

            // Act & Assert
            mockMvc.perform(get(APICATEGORY))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[0].categoryId").value(1))
                    .andExpect(jsonPath("$.data[0].categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.data[1].categoryId").value(2))
                    .andExpect(jsonPath("$.data[1].categoryName").value("Tecnología"));

            // Verify
            verify(categoryService).findAll();
        }

        // test find all pageable
        @Test
        void givenCategoriesExist_whenGetPage_thenReturnPagedCategoryList() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryId").ascending());
            Page<CategoryDTOResponse> pagedCategories = new PageImpl<>(categoryDTOResponseList, pageable, categoryDTOResponseList.size());

            when(categoryService.findAllPage(any(Pageable.class))).thenReturn(pagedCategories);
            // Act & Assert
            mockMvc.perform(get(APICATEGORY + "/page")
                            .param("page","0")
                            .param("size","2")
                            .param("sort","categoryId"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].categoryId").value(1))
                    .andExpect(jsonPath("$.content[0].categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.content[1].categoryId").value(2))
                    .andExpect(jsonPath("$.content[1].categoryName").value("Tecnología"));

            // Verify
            verify(categoryService).findAllPage(any(Pageable.class));
        }

        // listado vacio paginado
        @Test
        void givenNoCategories_whenGetPage_thenReturnEmptyPage() throws Exception {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryId").ascending());
            Page<CategoryDTOResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(categoryService.findAllPage(any(Pageable.class))).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(APICATEGORY + "/page")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "categoryId,asc"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(0)));

            // Verify
            verify(categoryService).findAllPage(any(Pageable.class));
        }

        // find by categoryId
        @Test
        void givenValidId_whenGetById_thenReturnCategory() throws Exception {

            // Arrange
            Long idCategoryExist = 1L;
            when(categoryService.findById(idCategoryExist)).thenReturn(categoryDTOResponse);

            // Act & Assert
            mockMvc.perform(get(APICATEGORY + "/"+idCategoryExist))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data[0].categoryId").value(1L))
                    .andExpect(jsonPath("$.data[0].categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.data[0].categoryDescription").value("Categoría de ropa"))
                    .andExpect(jsonPath("$.data[0].categoryEnabled").value(true));

            // Verify
            verify( categoryService).findById(idCategoryExist);

        }

        // find by categoryId non exist
        @Test
        void givenInvalidId_whenGetById_thenReturnNotFound() throws Exception {
            // Arrange
            Long idCategoryNonExist = 99L;
            when( categoryService.findById(idCategoryNonExist)).thenThrow(new ResourceNotFoundException("Category not found"));

            // Act & Assert
            mockMvc.perform(get(APICATEGORY + "/"+idCategoryNonExist))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.message").value(TestConstants.MESSAGE_NOT_FOUND));

            // Verify
            verify( categoryService).findById(idCategoryNonExist);
        }

    }

    @Nested
    class PostCategoryTests {

        // test de save
        @Test
        void shouldReturnCreatedCategory_whenValidCategoryIsPosted() throws Exception {
            // Arrange
            when(categoryService.save(categoryDTORequest)).thenReturn(categoryDTOResponse);

            // Act & Assert
            mockMvc.perform(post(APICATEGORY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDTORequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.message").value(TestConstants.MESSAGE_CREATED))
                    .andExpect(jsonPath("$.data[0].categoryId").value(1L))
                    .andExpect(jsonPath("$.data[0].categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.data[0].categoryDescription").value("Categoría de ropa"))
                    .andExpect(jsonPath("$.data[0].categoryEnabled").value(true));

            // Verify
            verify(categoryService).save(eq(categoryDTORequest));
        }


        // pat global invalid
        @ParameterizedTest
        @MethodSource("provideInvalidCategoryNames")
        void shouldReturnValidationError_whenCategoryName(String invalidName, String expectedMessageFragment) throws Exception {

            // Arrange
            String json = """
        {
          "categoryName": "%s",
          "categoryDescription": "Ropa deportiva",
          "categoryEnabled": true
        }
        """.formatted(invalidName);

            // Act & Assert
            mockMvc.perform(post(APICATEGORY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(categoryService);
        }

        private static Stream<Arguments> provideInvalidCategoryNames() {
            return Stream.of(
                    Arguments.of("", TestConstants.ERROR_REQUIRED), // @NotBlank
                    Arguments.of("A", TestConstants.ERROR_SIZE_NAME), // @Size
                    Arguments.of("👻", TestConstants.ERROR_INVALID_FORMAT) // @Pattern
            );
        }

        // categoryDescription
        @ParameterizedTest
        @MethodSource("provideInvalidCategoryDescription")
        void shouldReturnValidationError_whenCategoryDescription(String invalidName, String expectedMessageFragment) throws Exception {

            // Arrange
            String json = """
        {
          "categoryName": "name",
          "categoryDescription": "%s",
          "categoryEnabled": true
        }
        """.formatted(invalidName);

            // Act & Assert
            mockMvc.perform(post(APICATEGORY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryDescription")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions(categoryService);
        }

        private static Stream<Arguments> provideInvalidCategoryDescription() {
            return Stream.of(
                    Arguments.of("", TestConstants.ERROR_REQUIRED), // @NotBlank
                    Arguments.of("A", TestConstants.ERROR_SIZE_DESCRIPTION), // @Size
                    Arguments.of("👻", TestConstants.ERROR_INVALID_FORMAT) // @Pattern
            );
        }


        // @NotNull categoryEnabled
        @Test
        void shouldReturnValidationError_whenCategoryEnabledIsNull() throws Exception {
            // Arrange
            String invalidJson = """
            {
              "categoryName": "category",
              "categoryDescription": "descrption",
              "categoryEnabled": null
            }
            """;

            // Act & Assert
            mockMvc.perform(post(APICATEGORY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryEnabled")))
                    .andExpect(jsonPath("$.message").value(containsString(TestConstants.ERROR_REQUIRED)))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions( categoryService);
        }
        // JSON INVALID
        @Test
        void shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
            // Arrange
            String invalidJson = """
            {
              "categoryName": "category",
              "categoryDescription": "descrption",
              "categoryEnabled": yes
            }
            """;

            // Act & Assert
            mockMvc.perform(post(APICATEGORY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value(("JSON bad format.")))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verifyNoInteractions( categoryService);
        }
    }

    @Nested
    class PutCategoryTests {

        // test de update
        @Test
        void shouldReturnUpdatedCategory_whenValidIdAndDataProvided() throws Exception {
            // Arrange
            Long idCategoryExist = 1L;
            CategoryDTORequest updateRequest = new CategoryDTORequest("Ropa Deportiva", "Ropa para deporte", true);
            CategoryDTOResponse updatedResponse = new CategoryDTOResponse(idCategoryExist, "Ropa Deportiva", "Ropa para deporte", true);

            when(categoryService.update(eq(idCategoryExist), any(CategoryDTORequest.class)))
                    .thenReturn(updatedResponse);
            // Act & Assert
            mockMvc.perform(put(APICATEGORY + "/" + idCategoryExist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("200"))
                    .andExpect(jsonPath("$.message").value(TestConstants.MESSAGE_UPDATED))
                    .andExpect(jsonPath("$.data[0].categoryId").value(1L))
                    .andExpect(jsonPath("$.data[0].categoryName").value("Ropa Deportiva"))
                    .andExpect(jsonPath("$.data[0].categoryDescription").value("Ropa para deporte"))
                    .andExpect(jsonPath("$.data[0].categoryEnabled").value(true));
            // Verify
            verify(categoryService).update(eq(idCategoryExist), any(CategoryDTORequest.class));
        }

        // test update fail id
        @Test
        void shouldReturnNotFound_whenUpdatingNonExistentCategory() throws Exception {
            // Arrange
            Long idCategoryNonExist = 99L;
            CategoryDTORequest updateRequest = new CategoryDTORequest("Ropa Deportiva", "Ropa para deporte", true);

            when(categoryService.update(eq(idCategoryNonExist), any(CategoryDTORequest.class)))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            // Act & Assert
            mockMvc.perform(put(APICATEGORY + "/" + idCategoryNonExist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.message").value(TestConstants.MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.path").value(APICATEGORY + "/" + idCategoryNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(categoryService, times(1)).update(eq(idCategoryNonExist), any(CategoryDTORequest.class));
            verifyNoMoreInteractions(categoryService);
        }
        // @Valid

        @ParameterizedTest
        @MethodSource("provideInvalidCategoryName")
        void shouldReturnValidationError_whenCategoryName(String invalidName,String expectedMessageFragment) throws Exception{
            // Arrange
            Long id = 1L;
            String json = """
        {
          "categoryName": "%s",
          "categoryDescription": "Ropa deportiva",
          "categoryEnabled": true
        }
        """.formatted(invalidName);

            // Act & Assert
            mockMvc.perform(put(APICATEGORY + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryName")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories/" + id))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoInteractions(categoryService);
        }



        private static Stream<Arguments> provideInvalidCategoryName() {
            return Stream.of(
                    Arguments.of("", TestConstants.ERROR_REQUIRED), // @NotBlank
                    Arguments.of("A", TestConstants.ERROR_SIZE_NAME), // @Size
                    Arguments.of("👻", TestConstants.ERROR_INVALID_FORMAT) // @Pattern
            );
        }

        // categoryDescription
        @ParameterizedTest
        @MethodSource("provideInvalidCategoryDescription")
        void shouldReturnValidationError_whenCategoryDescription(String invalidName,String expectedMessageFragment) throws Exception{
            // Arrange
            Long id = 1L;
            String json = """
        {
          "categoryName": "name",
          "categoryDescription": "%s",
          "categoryEnabled": true
        }
        """.formatted(invalidName);

            // Act & Assert
            mockMvc.perform(put(APICATEGORY + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryDescription")))
                    .andExpect(jsonPath("$.message").value(containsString(expectedMessageFragment)))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories/" + id))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoInteractions(categoryService);
        }



        private static Stream<Arguments> provideInvalidCategoryDescription() {
            return Stream.of(
                    Arguments.of("", TestConstants.ERROR_REQUIRED), // @NotBlank
                    Arguments.of("A", TestConstants.ERROR_SIZE_DESCRIPTION), // @Size
                    Arguments.of("👻", TestConstants.ERROR_INVALID_FORMAT) // @Pattern
            );
        }

        // @NotNull
        @Test
        void shouldReturnValidationError_whenCategoryEnabledIsNull() throws Exception {
            // Arrange
            Long id = 1L;
            String invalidJson = """
    {
      "categoryName": "name",
      "categoryDescription": "descrption",
      "categoryEnabled": null
    }
    """;
            // Act & Assert

            mockMvc.perform(put(APICATEGORY + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value(containsString("categoryEnabled")))
                    .andExpect(jsonPath("$.message").value(containsString(TestConstants.ERROR_REQUIRED)))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories/" + id))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoInteractions(categoryService);
        }

        // JSON BAD FORMAT
        @Test
        void shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
            // Arrange
            Long id = 1L;
            String invalidJson = """
    {
      "categoryName": "name",
      "categoryDescription": "descrption",
      "categoryEnabled": yes
    }
    """;
            // Act & Assert

            mockMvc.perform(put(APICATEGORY + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("MalformedJsonError"))
                    .andExpect(jsonPath("$.message").value(("JSON bad format.")))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories/"+id))
                    .andExpect(jsonPath("$.timestamp").exists());

            // Verify
            verifyNoInteractions(categoryService);
        }

    }

    @Nested
    class DeleteCategoryTests {

        // delete id
        @Test
        void givenValidId_whenDelete_thenReturnSuccessMessage() throws Exception {
            // Arrange
            Long idCategoryExist = 1L;
            doNothing().when(categoryService).deleteById(idCategoryExist);

            // Act & Assert
            mockMvc.perform(delete(APICATEGORY + "/" + idCategoryExist))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("200"))
                    .andExpect(jsonPath("$.message").value(TestConstants.MESSAGE_DELETED));
            // Verify
            verify(categoryService).deleteById(idCategoryExist);
        }

        // delete id fail
        @Test
        void givenInvalidId_whenDelete_thenReturnNotFound() throws Exception {
            // Arrange
            Long idCategoryNonExist = 99L;
            doThrow(new ResourceNotFoundException("Category not found"))
                    .when(categoryService).deleteById(idCategoryNonExist);
            // Act & Assert
            mockMvc.perform(delete(APICATEGORY + "/" + idCategoryNonExist))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.message").value(TestConstants.MESSAGE_NOT_FOUND))
                    .andExpect(jsonPath("$.path").value(APICATEGORY + "/" + idCategoryNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(categoryService).deleteById(idCategoryNonExist);
        }

    }
}
