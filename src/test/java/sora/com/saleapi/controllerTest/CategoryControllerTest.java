package sora.com.saleapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
                    .andExpect(jsonPath("$.message").value("Category not found"));

            // Verify
            verify( categoryService).findById(idCategoryNonExist);
        }

    }

    @Nested
    class PostCategoryTests {

        // test de save
        @Test
        void givenValidCategory_whenPost_thenReturnCreatedCategory() throws Exception {
            // Arrange
            when(categoryService.save(categoryDTORequest)).thenReturn(categoryDTOResponse);

            // Act & Assert
            mockMvc.perform(post(APICATEGORY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDTORequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("201"))
                    .andExpect(jsonPath("$.message").value("Creado correctamente"))
                    .andExpect(jsonPath("$.data[0].categoryId").value(1L))
                    .andExpect(jsonPath("$.data[0].categoryName").value("Ropa"))
                    .andExpect(jsonPath("$.data[0].categoryDescription").value("Categoría de ropa"))
                    .andExpect(jsonPath("$.data[0].categoryEnabled").value(true));

            // Verify
            verify(categoryService).save(any(CategoryDTORequest.class));
        }

        // test de save invalid
        // not empty categoryName
        @Test
        void givenEmptyCategoryName_whenPost_thenReturnBadRequestWithCustomValidationMessage() throws Exception {
            // Arrange
            String invalidJson = """
            {
              "categoryName": "",
              "categoryDescription": "Tiempo y detalles",
              "categoryEnabled": true
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
                    .andExpect(jsonPath("$.message").value ("categoryName: El nombre de la categoría no debe estar vacio"))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        // not empty categoryDescription
        @Test
        void givenEmptyCategoryDescription_whenPost_thenReturnBadRequestWithCustomValidationMessage() throws Exception {
            // Arrange
            String invalidJson = """
            {
              "categoryName": "Ropa",
              "categoryDescription": "",
              "categoryEnabled": true
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
                    .andExpect(jsonPath("$.message").value("categoryDescription: El campo de descripcion no debe estar vacio"))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        // not null categoryEnabled
        @Test
        void givenNullCategoryEnabled_whenPost_thenReturnBadRequestWithCustomValidationMessage() throws Exception {
            // Arrange
            String invalidJson = """
            {
              "categoryName": "Ropa",
              "categoryDescription": "Descripción válida",
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
                    .andExpect(jsonPath("$.message").value("categoryEnabled: El campo enabled no debe ser null"))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        // not null categoryName
        @Test
        void givenNullCategoryName_whenPost_thenReturnBadRequestWithMultipleValidationMessages() throws Exception {
            // Arrange
            String invalidJson = """
        {
          "categoryName": null,
          "categoryDescription": "Descripción válida",
          "categoryEnabled": true
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
                    .andExpect(jsonPath("$.message", containsString("categoryName: El nombre de la categoría no debe estar vacio")))
                    .andExpect(jsonPath("$.message", containsString("categoryName: El nombre de la categoría es obligatorio")))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        // not null categoryDescription
        @Test
        void givenNullCategoryDescription_whenPost_thenReturnBadRequestWithMultipleValidationMessages() throws Exception {
            // Arrange
            String invalidJson = """
        {
          "categoryName": "Ropa",
          "categoryDescription": null,
          "categoryEnabled": true
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
                    .andExpect(jsonPath("$.message", containsString("categoryDescription: El campo de descripcion no debe estar vacio")))
                    .andExpect(jsonPath("$.message",containsString("categoryDescription: El campo de descripcion no debe ser null")))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }


    }

    @Nested
    class PutCategoryTests {

        // test de update
        @Test
        void givenValidIdAndData_whenPut_thenReturnUpdatedCategory() throws Exception {
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
                    .andExpect(jsonPath("$.message").value("Actualizado correctamente"))
                    .andExpect(jsonPath("$.data[0].categoryId").value(1L))
                    .andExpect(jsonPath("$.data[0].categoryName").value("Ropa Deportiva"))
                    .andExpect(jsonPath("$.data[0].categoryDescription").value("Ropa para deporte"))
                    .andExpect(jsonPath("$.data[0].categoryEnabled").value(true));
            // Verify
            verify(categoryService).update(eq(idCategoryExist), any(CategoryDTORequest.class));
        }

        // test update fail id
        @Test
        void givenInvalidId_whenPut_thenReturnNotFound() throws Exception {
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
                    .andExpect(jsonPath("$.message").value("Category not found"))
                    .andExpect(jsonPath("$.errorType").value("ResourceNotFound"))
                    .andExpect(jsonPath("$.path").value(APICATEGORY + "/" + idCategoryNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(categoryService).update(eq(idCategoryNonExist), any(CategoryDTORequest.class));
        }

        // not empty category Name
        @Test
        void givenEmptyCategoryName_whenPut_thenReturnBadRequest() throws Exception {
            Long id = 1L;
            String invalidJson = """
    {
      "categoryName": "",
      "categoryDescription": "Tiempo y detalles",
      "categoryEnabled": true
    }
    """;

            mockMvc.perform(put(APICATEGORY + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.errorType").value("ValidationError"))
                    .andExpect(jsonPath("$.message").value("categoryName: El nombre de la categoría no debe estar vacio"))
                    .andExpect(jsonPath("$.path").value("/api/v1/categories/" + id))
                    .andExpect(jsonPath("$.timestamp").exists());
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
                    .andExpect(jsonPath("$.message").value("Eliminado correctamente"));
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
                    .andExpect(jsonPath("$.message").value("Category not found"))
                    .andExpect(jsonPath("$.path").value(APICATEGORY + "/" + idCategoryNonExist))
                    .andExpect(jsonPath("$.timestamp").exists());
            // Verify
            verify(categoryService).deleteById(idCategoryNonExist);
        }
    }
}
