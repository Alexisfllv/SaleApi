package sora.com.saleapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    // test de listar
    @Test
    public void getAllCategories() throws Exception {
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
    void getCategoryPage() throws Exception {
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

    // find by categoryId
    @Test
    void findbyCategoryId() throws Exception {

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
    void findCateIdNonExist() throws Exception {
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


    // test de save
    @Test
    void saveCategory() throws Exception {
        // Arrange
        when(categoryService.save(categoryDTORequest)).thenReturn(categoryDTOResponse);

        // Act & Assert
        mockMvc.perform(post(APICATEGORY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[0].categoryId").value(1L))
                .andExpect(jsonPath("$.data[0].categoryName").value("Ropa"))
                .andExpect(jsonPath("$.data[0].categoryDescription").value("Categoría de ropa"))
                .andExpect(jsonPath("$.data[0].categoryEnabled").value(true));

        // Verify
        verify(categoryService).save(any(CategoryDTORequest.class));
    }





}
