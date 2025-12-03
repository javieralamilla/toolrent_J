package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.ToolsInventoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ToolsInventoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ToolsInventoryRepository toolsInventoryRepository;

    @Test
    public void whenFindByName_thenReturnToolInventory() {
        // given
        ToolsInventoryEntity inventory = new ToolsInventoryEntity(
                null,
                "Taladro",
                "Herramientas Eléctricas",
                10,
                7,
                50000,
                5000);
        entityManager.persistAndFlush(inventory);

        // when
        ToolsInventoryEntity found = toolsInventoryRepository.findByName("Taladro");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Taladro");
        assertThat(found.getTotalTools()).isEqualTo(10);
        assertThat(found.getCurrentStock()).isEqualTo(7);
    }

    @Test
    public void whenFindByNameAndCategory_thenReturnToolInventory() {
        // given
        ToolsInventoryEntity inventory1 = new ToolsInventoryEntity(
                null,
                "Martillo",
                "Herramientas Manuales",
                15,
                12,
                10000,
                2000);
        ToolsInventoryEntity inventory2 = new ToolsInventoryEntity(
                null,
                "Martillo",
                "Herramientas Eléctricas",
                8,
                5,
                30000,
                4000);
        entityManager.persist(inventory1);
        entityManager.persist(inventory2);
        entityManager.flush();

        // when
        ToolsInventoryEntity found = toolsInventoryRepository.findByNameAndCategory(
                "Martillo", "Herramientas Manuales");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Martillo");
        assertThat(found.getCategory()).isEqualTo("Herramientas Manuales");
        assertThat(found.getTotalTools()).isEqualTo(15);
    }

    @Test
    public void whenFindByCategory_thenReturnToolInventories() {
        // given
        ToolsInventoryEntity inventory1 = new ToolsInventoryEntity(
                null,
                "Taladro",
                "Herramientas Eléctricas",
                10,
                7,
                50000,
                5000);
        ToolsInventoryEntity inventory2 = new ToolsInventoryEntity(
                null,
                "Sierra",
                "Herramientas Eléctricas",
                5,
                3,
                40000,
                4500);
        ToolsInventoryEntity inventory3 = new ToolsInventoryEntity(
                null,
                "Martillo",
                "Herramientas Manuales",
                15,
                12,
                10000,
                2000);
        entityManager.persist(inventory1);
        entityManager.persist(inventory2);
        entityManager.persist(inventory3);
        entityManager.flush();

        // when
        List<ToolsInventoryEntity> foundInventories = toolsInventoryRepository.findByCategory("Herramientas Eléctricas");

        // then
        assertThat(foundInventories).hasSize(2);
        assertThat(foundInventories).extracting(ToolsInventoryEntity::getCategory)
                .containsOnly("Herramientas Eléctricas");
        assertThat(foundInventories).extracting(ToolsInventoryEntity::getName)
                .containsExactlyInAnyOrder("Taladro", "Sierra");
    }
}