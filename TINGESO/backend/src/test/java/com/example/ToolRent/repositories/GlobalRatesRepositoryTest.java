package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.GlobalRatesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class GlobalRatesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GlobalRatesRepository globalRatesRepository;

    @Test
    public void whenFindGlobalRatesById_thenReturnRate() {
        // given
        GlobalRatesEntity rate = new GlobalRatesEntity(
                null,
                "Tarifa Diaria de Multa",
                1000);
        entityManager.persistAndFlush(rate);

        // when
        Optional<GlobalRatesEntity> found = globalRatesRepository.findGlobalRatesById(rate.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getRateName()).isEqualTo("Tarifa Diaria de Multa");
        assertThat(found.get().getDailyRateValue()).isEqualTo(1000);
    }

    @Test
    public void whenFindByRateName_thenReturnRate() {
        // given
        GlobalRatesEntity rate = new GlobalRatesEntity(
                null,
                "Tarifa Diaria de Multa",
                1000);
        entityManager.persistAndFlush(rate);

        // when
        GlobalRatesEntity found = globalRatesRepository.findByRateName("Tarifa Diaria de Multa");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getRateName()).isEqualTo("Tarifa Diaria de Multa");
        assertThat(found.getDailyRateValue()).isEqualTo(1000);
    }
}