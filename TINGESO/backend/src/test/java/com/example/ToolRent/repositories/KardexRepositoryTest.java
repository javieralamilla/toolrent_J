package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.KardexEntity;
import com.example.ToolRent.entities.ToolEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class KardexRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KardexRepository kardexRepository;

    @Test
    public void whenFindByToolOrderByDateDesc_thenReturnMovementsOrderedByDate() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        ToolEntity tool = new ToolEntity(null, "Taladro", category, "AVAILABLE");

        entityManager.persist(category);
        entityManager.persist(tool);

        KardexEntity movement1 = new KardexEntity(null, "ingreso",
                LocalDate.of(2025, 11, 1), "admin", tool, 10);
        KardexEntity movement2 = new KardexEntity(null, "prestamo",
                LocalDate.of(2025, 11, 15), "empleado1", tool, 2);
        KardexEntity movement3 = new KardexEntity(null, "devolucion",
                LocalDate.of(2025, 11, 20), "empleado1", tool, 2);

        entityManager.persist(movement1);
        entityManager.persist(movement2);
        entityManager.persist(movement3);
        entityManager.flush();

        // when
        List<KardexEntity> movements = kardexRepository.findByToolOrderByDateDesc(tool.getId());

        // then
        assertThat(movements).hasSize(3);
        assertThat(movements.get(0).getDate()).isEqualTo(LocalDate.of(2025, 11, 20));
        assertThat(movements.get(1).getDate()).isEqualTo(LocalDate.of(2025, 11, 15));
        assertThat(movements.get(2).getDate()).isEqualTo(LocalDate.of(2025, 11, 1));
    }

    @Test
    public void whenFindByDateBetweenOrderByDateDesc_thenReturnMovementsInRange() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Herramientas Manuales");
        ToolEntity tool1 = new ToolEntity(null, "Martillo", category, "AVAILABLE");
        ToolEntity tool2 = new ToolEntity(null, "Destornillador", category, "AVAILABLE");

        entityManager.persist(category);
        entityManager.persist(tool1);
        entityManager.persist(tool2);

        KardexEntity movement1 = new KardexEntity(null, "ingreso",
                LocalDate.of(2025, 11, 5), "admin", tool1, 5);
        KardexEntity movement2 = new KardexEntity(null, "prestamo",
                LocalDate.of(2025, 11, 15), "empleado1", tool2, 3);
        KardexEntity movement3 = new KardexEntity(null, "devolucion",
                LocalDate.of(2025, 12, 5), "empleado1", tool1, 2);

        entityManager.persist(movement1);
        entityManager.persist(movement2);
        entityManager.persist(movement3);
        entityManager.flush();

        // when
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 30);
        List<KardexEntity> movements = kardexRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);

        // then
        assertThat(movements).hasSize(2);
        assertThat(movements).extracting(KardexEntity::getType)
                .containsExactly("prestamo", "ingreso");
    }

    @Test
    public void whenFindByToolIdAndDateBetween_thenReturnToolMovementsInRange() {
        // given
        CategoryEntity category = new CategoryEntity(null, "Equipo de Jardinería");
        ToolEntity tool = new ToolEntity(null, "Cortacésped", category, "AVAILABLE");

        entityManager.persist(category);
        entityManager.persist(tool);

        KardexEntity movement1 = new KardexEntity(null, "ingreso",
                LocalDate.of(2025, 11, 1), "admin", tool, 3);
        KardexEntity movement2 = new KardexEntity(null, "prestamo",
                LocalDate.of(2025, 11, 10), "empleado1", tool, 1);
        KardexEntity movement3 = new KardexEntity(null, "devolucion",
                LocalDate.of(2025, 11, 20), "empleado1", tool, 1);
        KardexEntity movement4 = new KardexEntity(null, "reparacion",
                LocalDate.of(2025, 12, 5), "tecnico", tool, 1);

        entityManager.persist(movement1);
        entityManager.persist(movement2);
        entityManager.persist(movement3);
        entityManager.persist(movement4);
        entityManager.flush();

        // when
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 30);
        List<KardexEntity> movements = kardexRepository.findByToolIdAndDateBetween(
                tool.getId(), startDate, endDate);

        // then
        assertThat(movements).hasSize(3);
        assertThat(movements.get(0).getType()).isEqualTo("devolucion");
        assertThat(movements.get(1).getType()).isEqualTo("prestamo");
        assertThat(movements.get(2).getType()).isEqualTo("ingreso");
    }
}