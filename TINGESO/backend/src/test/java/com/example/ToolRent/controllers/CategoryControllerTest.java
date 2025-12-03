package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CategoryController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    public void listAllCategories_ShouldReturnCategories() throws Exception {
        CategoryEntity category1 = new CategoryEntity(1L, "Herramientas Eléctricas");
        CategoryEntity category2 = new CategoryEntity(2L, "Herramientas Manuales");

        ArrayList<CategoryEntity> categoryList = new ArrayList<>(Arrays.asList(category1, category2));

        given(categoryService.ListAllCategories()).willReturn(categoryList);

        mockMvc.perform(get("/api/v1/categories/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Herramientas Eléctricas")))
                .andExpect(jsonPath("$[1].name", is("Herramientas Manuales")));
    }

    @Test
    public void listAllCategories_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        ArrayList<CategoryEntity> emptyList = new ArrayList<>();

        given(categoryService.ListAllCategories()).willReturn(emptyList);

        mockMvc.perform(get("/api/v1/categories/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}