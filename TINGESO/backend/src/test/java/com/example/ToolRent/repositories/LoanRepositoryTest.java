package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.entities.LoanEntity;
import com.example.ToolRent.entities.ToolEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    public void whenFindLoanById_thenReturnLoan() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Juan Pérez", "12345678-9",
                "juan@email.com", "+56912345678", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        ToolEntity tool = new ToolEntity(null, "Taladro", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(5), "activo", 5000);
        entityManager.persistAndFlush(loan);

        // when
        Optional<LoanEntity> found = loanRepository.findLoanById(loan.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo("activo");
    }

    @Test
    public void whenFindByReturnDate_thenReturnLoans() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "María González", "98765432-1",
                "maria@email.com", "+56987654321", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Manuales");
        ToolEntity tool = new ToolEntity(null, "Martillo", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LocalDate returnDate = LocalDate.of(2025, 12, 31);
        LoanEntity loan1 = new LoanEntity(null, customer, tool,
                LocalDate.now(), returnDate, "activo", 3000);
        LoanEntity loan2 = new LoanEntity(null, customer, tool,
                LocalDate.now(), returnDate, "vencido", 3000);
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.flush();

        // when
        Optional<List<LoanEntity>> foundLoans = loanRepository.findByReturnDate(returnDate);

        // then
        assertThat(foundLoans).isPresent();
        assertThat(foundLoans.get()).hasSize(2);
    }

    @Test
    public void whenFindByStatus_thenReturnLoans() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Pedro Silva", "11111111-1",
                "pedro@email.com", "+56911111111", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Equipo de Jardinería");
        ToolEntity tool = new ToolEntity(null, "Cortacésped", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LoanEntity loan1 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(3), "activo", 2000);
        LoanEntity loan2 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(7), "activo", 4000);
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.flush();

        // when
        Optional<List<LoanEntity>> foundLoans = loanRepository.findByStatus("activo");

        // then
        assertThat(foundLoans).isPresent();
        assertThat(foundLoans.get()).hasSize(2)
                .extracting(LoanEntity::getStatus).containsOnly("activo");
    }

    @Test
    public void whenFindByCustomerRut_thenReturnLoans() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Ana Torres", "22222222-2",
                "ana@email.com", "+56922222222", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        ToolEntity tool = new ToolEntity(null, "Sierra", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(5), "activo", 3500);
        entityManager.persistAndFlush(loan);

        // when
        Optional<List<LoanEntity>> foundLoans = loanRepository.findByCustomerRut("22222222-2");

        // then
        assertThat(foundLoans).isPresent();
        assertThat(foundLoans.get()).hasSize(1);
        assertThat(foundLoans.get().get(0).getCustomer().getRut()).isEqualTo("22222222-2");
    }

    @Test
    public void whenFindByCustomerRutAndStatus_thenReturnLoans() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Carlos Ramírez", "33333333-3",
                "carlos@email.com", "+56933333333", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Manuales");
        ToolEntity tool = new ToolEntity(null, "Destornillador", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LoanEntity loan1 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(3), "activo", 1500);
        LoanEntity loan2 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().minusDays(1), "vencido", 2000);
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.flush();

        // when
        Optional<List<LoanEntity>> foundLoans = loanRepository.findByCustomerRutAndStatus("33333333-3", "activo");

        // then
        assertThat(foundLoans).isPresent();
        assertThat(foundLoans.get()).hasSize(1);
        assertThat(foundLoans.get().get(0).getStatus()).isEqualTo("activo");
    }

    @Test
    public void whenFindActiveLoansByDateRange_thenReturnLoans() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Laura Díaz", "44444444-4",
                "laura@email.com", "+56944444444", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Equipo de Construcción");
        ToolEntity tool = new ToolEntity(null, "Taladro", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 30);

        LoanEntity loan1 = new LoanEntity(null, customer, tool,
                LocalDate.of(2025, 11, 10), LocalDate.of(2025, 11, 15), "activo", 2500);
        LoanEntity loan2 = new LoanEntity(null, customer, tool,
                LocalDate.of(2025, 11, 20), LocalDate.of(2025, 11, 25), "vencido", 3000);
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.flush();

        // when
        List<LoanEntity> foundLoans = loanRepository.findActiveLoansByDateRange(startDate, endDate);

        // then
        assertThat(foundLoans).hasSize(2);
    }

    @Test
    public void whenFindAllActiveLoans_thenReturnLoans() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Roberto Vega", "55555555-5",
                "roberto@email.com", "+56955555555", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas de Medición");
        ToolEntity tool = new ToolEntity(null, "Nivel", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LoanEntity loan1 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(5), "activo", 2000);
        LoanEntity loan2 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().minusDays(1), "vencido", 2500);
        LoanEntity loan3 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(3), "finalizado", 1500);
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.persist(loan3);
        entityManager.flush();

        // when
        List<LoanEntity> foundLoans = loanRepository.findAllActiveLoans();

        // then
        assertThat(foundLoans).hasSize(2);
    }

    @Test
    public void whenFindMostRentedToolsByDateRange_thenReturnRanking() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Sandra López", "66666666-6",
                "sandra@email.com", "+56966666666", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        ToolEntity tool1 = new ToolEntity(null, "Taladro", category, "AVAILABLE");
        ToolEntity tool2 = new ToolEntity(null, "Sierra", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool1);
        entityManager.persist(tool2);

        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 30);

        LoanEntity loan1 = new LoanEntity(null, customer, tool1,
                LocalDate.of(2025, 11, 5), LocalDate.of(2025, 11, 10), "finalizado", 2500);
        LoanEntity loan2 = new LoanEntity(null, customer, tool1,
                LocalDate.of(2025, 11, 15), LocalDate.of(2025, 11, 20), "finalizado", 2500);
        LoanEntity loan3 = new LoanEntity(null, customer, tool2,
                LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28), "activo", 1500);
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.persist(loan3);
        entityManager.flush();

        // when
        List<Object[]> ranking = loanRepository.findMostRentedToolsByDateRange(startDate, endDate);

        // then
        assertThat(ranking).isNotEmpty();
        assertThat(ranking.get(0)[0]).isEqualTo("Taladro");
        assertThat(ranking.get(0)[2]).isEqualTo(2L);
    }

    @Test
    public void whenFindMostRentedTools_thenReturnRanking() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Diego Morales", "77777777-7",
                "diego@email.com", "+56977777777", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Manuales");
        ToolEntity tool = new ToolEntity(null, "Martillo", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LoanEntity loan1 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(3), "activo", 1500);
        LoanEntity loan2 = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(5), "finalizado", 2000);
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.flush();

        // when
        List<Object[]> ranking = loanRepository.findMostRentedTools();

        // then
        assertThat(ranking).isNotEmpty();
        assertThat(ranking.get(0)[0]).isEqualTo("Martillo");
        assertThat(ranking.get(0)[2]).isEqualTo(2L);
    }
}