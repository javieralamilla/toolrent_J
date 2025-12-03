package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.ToolEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ToolRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ToolRepository toolRepository;

    @Test
    public void whenFindByName_thenReturnTools() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        entityManager.persist(category);

        ToolEntity tool1 = new ToolEntity(null, "Taladro", category, "AVAILABLE");
        ToolEntity tool2 = new ToolEntity(null, "Taladro", category, "RENTED");
        entityManager.persist(tool1);
        entityManager.persist(tool2);
        entityManager.flush();

        // when
        List<ToolEntity> foundTools = toolRepository.findByName("Taladro");

        // then
        assertThat(foundTools).hasSize(2).extracting(ToolEntity::getName).containsOnly("Taladro");
    }

    @Test
    public void whenFindByStatus_thenReturnTools() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Herramientas Manuales");
        entityManager.persist(category);

        ToolEntity tool1 = new ToolEntity(null, "Martillo", category, "AVAILABLE");
        ToolEntity tool2 = new ToolEntity(null, "Destornillador", category, "AVAILABLE");
        entityManager.persist(tool1);
        entityManager.persist(tool2);
        entityManager.flush();

        // when
        List<ToolEntity> foundTools = toolRepository.findByStatus("AVAILABLE");

        // then
        assertThat(foundTools).hasSize(2).extracting(ToolEntity::getStatus).containsOnly("AVAILABLE");
    }

    @Test
    public void whenFindByCategory_thenReturnTools() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        entityManager.persist(category);

        ToolEntity tool1 = new ToolEntity(null, "Taladro", category, "AVAILABLE");
        ToolEntity tool2 = new ToolEntity(null, "Sierra", category, "RENTED");
        entityManager.persist(tool1);
        entityManager.persist(tool2);
        entityManager.flush();

        // when
        List<ToolEntity> foundTools = toolRepository.findByCategory("Herramientas Eléctricas");

        // then
        assertThat(foundTools).hasSize(2)
                .extracting(tool -> tool.getCategory().getName())
                .containsOnly("Herramientas Eléctricas");
    }

    @Test
    public void whenFindToolById_thenReturnTool() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Equipo de Jardinería");
        entityManager.persist(category);

        ToolEntity tool = new ToolEntity(null, "Cortacésped", category, "AVAILABLE");
        entityManager.persistAndFlush(tool);

        // when
        ToolEntity found = toolRepository.findToolById(tool.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Cortacésped");
    }

    @Test
    public void whenFindToolByNameAndCategory_thenReturnTool() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        entityManager.persist(category);

        ToolEntity tool = new ToolEntity(null, "Taladro", category, "AVAILABLE");
        entityManager.persistAndFlush(tool);

        // when
        Optional<ToolEntity> found = toolRepository.findToolByNameAndCategory("Taladro", "Herramientas Eléctricas");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Taladro");
        assertThat(found.get().getCategory().getName()).isEqualTo("Herramientas Eléctricas");
    }
}