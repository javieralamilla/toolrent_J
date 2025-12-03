package com.example.ToolRent.services;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryEntity categoryEntity1;
    private CategoryEntity categoryEntity2;

    @BeforeEach
    void setUp() {
        categoryEntity1 = new CategoryEntity();
        categoryEntity1.setId(1L);
        categoryEntity1.setName("Herramientas Eléctricas");

        categoryEntity2 = new CategoryEntity();
        categoryEntity2.setId(2L);
        categoryEntity2.setName("Herramientas Manuales");
    }

    // ==================== ListAllCategories ====================

    @Test
    void whenListAllCategories_thenReturnAllCategories() {
        //Given
        List<CategoryEntity> categoriesList = new ArrayList<>();
        categoriesList.add(categoryEntity1);
        categoriesList.add(categoryEntity2);
        when(categoryRepository.findAll()).thenReturn(categoriesList);

        //When
        ArrayList<CategoryEntity> result = categoryService.ListAllCategories();

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("Herramientas Eléctricas");
        assertThat(result.get(1).getName()).isEqualTo("Herramientas Manuales");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void whenListAllCategoriesWithEmptyList_thenReturnEmptyList() {
        //Given
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        //When
        ArrayList<CategoryEntity> result = categoryService.ListAllCategories();

        //Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void whenListAllCategoriesWithSingleCategory_thenReturnSingleCategory() {
        //Given
        List<CategoryEntity> categoriesList = new ArrayList<>();
        categoriesList.add(categoryEntity1);
        when(categoryRepository.findAll()).thenReturn(categoriesList);

        //When
        ArrayList<CategoryEntity> result = categoryService.ListAllCategories();

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Herramientas Eléctricas");
        verify(categoryRepository, times(1)).findAll();
    }

    // ==================== isCategoryExists ====================

    @Test
    void whenIsCategoryExistsWithExistingCategory_thenReturnTrue() {
        //Given
        String categoryName = "Herramientas Eléctricas";
        when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.of(categoryEntity1));

        //When
        boolean result = categoryService.isCategoryExists(categoryName);

        //Then
        assertThat(result).isTrue();
        verify(categoryRepository, times(1)).findCategoryByName(categoryName);
    }

    @Test
    void whenIsCategoryExistsWithNonExistingCategory_thenReturnFalse() {
        //Given
        String categoryName = "Categoría Inexistente";
        when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.empty());

        //When
        boolean result = categoryService.isCategoryExists(categoryName);

        //Then
        assertThat(result).isFalse();
        verify(categoryRepository, times(1)).findCategoryByName(categoryName);
    }

    @Test
    void whenIsCategoryExistsWithEmptyString_thenReturnFalse() {
        //Given
        String categoryName = "";
        when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.empty());

        //When
        boolean result = categoryService.isCategoryExists(categoryName);

        //Then
        assertThat(result).isFalse();
        verify(categoryRepository, times(1)).findCategoryByName(categoryName);
    }

    @Test
    void whenIsCategoryExistsWithNullString_thenReturnFalse() {
        //Given
        String categoryName = null;
        when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.empty());

        //When
        boolean result = categoryService.isCategoryExists(categoryName);

        //Then
        assertThat(result).isFalse();
        verify(categoryRepository, times(1)).findCategoryByName(categoryName);
    }

    @Test
    void whenIsCategoryExistsCalledMultipleTimes_thenRepositoryCalledEachTime() {
        //Given
        String categoryName = "Herramientas Eléctricas";
        when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.of(categoryEntity1));

        //When
        boolean result1 = categoryService.isCategoryExists(categoryName);
        boolean result2 = categoryService.isCategoryExists(categoryName);

        //Then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        verify(categoryRepository, times(2)).findCategoryByName(categoryName);
    }
}