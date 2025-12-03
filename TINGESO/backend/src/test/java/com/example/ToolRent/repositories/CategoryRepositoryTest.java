package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CategoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void whenFindCategoryByName_thenReturnCategory() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        entityManager.persistAndFlush(category);

        // when
        Optional<CategoryEntity> found = categoryRepository.findCategoryByName("Herramientas Eléctricas");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Herramientas Eléctricas");
    }

    @Test
    public void whenFindCategoryByName_WithNonExisting_thenReturnEmpty() {
        // when
        Optional<CategoryEntity> found = categoryRepository.findCategoryByName("Categoría Inexistente");

        // then
        assertThat(found).isEmpty();
    }
}