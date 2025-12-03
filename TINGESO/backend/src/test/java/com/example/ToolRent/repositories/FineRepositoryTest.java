package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.*;
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
class FineRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FineRepository fineRepository;

    @Test
    public void whenFindByStatus_thenReturnFines() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Juan Pérez", "12345678-9",
                "juan@email.com", "+56912345678", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        ToolEntity tool = new ToolEntity(null, "Taladro", category, "AVAILABLE");
        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(5), "activo", 5000);

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);
        entityManager.persist(loan);

        FineEntity fine1 = new FineEntity(null, customer, loan, "atraso", 2000, "no pagada");
        FineEntity fine2 = new FineEntity(null, customer, loan, "daño leve", 3000, "no pagada");
        entityManager.persist(fine1);
        entityManager.persist(fine2);
        entityManager.flush();

        // when
        Optional<List<FineEntity>> foundFines = fineRepository.findByStatus("no pagada");

        // then
        assertThat(foundFines).isPresent();
        assertThat(foundFines.get()).hasSize(2)
                .extracting(FineEntity::getStatus).containsOnly("no pagada");
    }

    @Test
    public void whenFindByType_thenReturnFines() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "María González", "98765432-1",
                "maria@email.com", "+56987654321", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Manuales");
        ToolEntity tool = new ToolEntity(null, "Martillo", category, "AVAILABLE");
        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(3), "vencido", 3000);

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);
        entityManager.persist(loan);

        FineEntity fine1 = new FineEntity(null, customer, loan, "atraso", 1500, "no pagada");
        FineEntity fine2 = new FineEntity(null, customer, loan, "atraso", 2000, "pagada");
        entityManager.persist(fine1);
        entityManager.persist(fine2);
        entityManager.flush();

        // when
        Optional<List<FineEntity>> foundFines = fineRepository.findByType("atraso");

        // then
        assertThat(foundFines).isPresent();
        assertThat(foundFines.get()).hasSize(2)
                .extracting(FineEntity::getType).containsOnly("atraso");
    }

    @Test
    public void whenFindFineByCustomerRut_thenReturnFines() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Pedro Silva", "11111111-1",
                "pedro@email.com", "+56911111111", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Equipo de Jardinería");
        ToolEntity tool = new ToolEntity(null, "Cortacésped", category, "AVAILABLE");
        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(7), "activo", 7000);

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);
        entityManager.persist(loan);

        FineEntity fine = new FineEntity(null, customer, loan, "daño irreparable", 5000, "no pagada");
        entityManager.persistAndFlush(fine);

        // when
        Optional<List<FineEntity>> foundFines = fineRepository.findFineByCustomerRut("11111111-1");

        // then
        assertThat(foundFines).isPresent();
        assertThat(foundFines.get()).hasSize(1);
        assertThat(foundFines.get().get(0).getCustomer().getRut()).isEqualTo("11111111-1");
    }

    @Test
    public void whenFindFineByCustomerRutAndStatus_thenReturnFines() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Ana Torres", "22222222-2",
                "ana@email.com", "+56922222222", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        ToolEntity tool = new ToolEntity(null, "Sierra", category, "AVAILABLE");
        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(5), "activo", 5000);

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);
        entityManager.persist(loan);

        FineEntity fine1 = new FineEntity(null, customer, loan, "atraso", 1000, "no pagada");
        FineEntity fine2 = new FineEntity(null, customer, loan, "daño leve", 2000, "pagada");
        entityManager.persist(fine1);
        entityManager.persist(fine2);
        entityManager.flush();

        // when
        Optional<List<FineEntity>> foundFines = fineRepository.findFineByCustomerRutAndStatus("22222222-2", "no pagada");

        // then
        assertThat(foundFines).isPresent();
        assertThat(foundFines.get()).hasSize(1);
        assertThat(foundFines.get().get(0).getStatus()).isEqualTo("no pagada");
    }

    @Test
    public void whenFindFineByCustomerRutAndType_thenReturnFines() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Carlos Ramírez", "33333333-3",
                "carlos@email.com", "+56933333333", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Manuales");
        ToolEntity tool = new ToolEntity(null, "Destornillador", category, "AVAILABLE");
        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(3), "activo", 3000);

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);
        entityManager.persist(loan);

        FineEntity fine1 = new FineEntity(null, customer, loan, "atraso", 1500, "no pagada");
        FineEntity fine2 = new FineEntity(null, customer, loan, "daño leve", 2500, "no pagada");
        entityManager.persist(fine1);
        entityManager.persist(fine2);
        entityManager.flush();

        // when
        Optional<List<FineEntity>> foundFines = fineRepository.findFineByCustomerRutAndType("33333333-3", "atraso");

        // then
        assertThat(foundFines).isPresent();
        assertThat(foundFines.get()).hasSize(1);
        assertThat(foundFines.get().get(0).getType()).isEqualTo("atraso");
    }

    @Test
    public void whenFindFineByCustomerRutAndStatusAndType_thenReturnFines() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Laura Díaz", "44444444-4",
                "laura@email.com", "+56944444444", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Equipo de Construcción");
        ToolEntity tool = new ToolEntity(null, "Taladro", category, "AVAILABLE");
        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().plusDays(5), "vencido", 5000);

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);
        entityManager.persist(loan);

        FineEntity fine1 = new FineEntity(null, customer, loan, "atraso", 2000, "no pagada");
        FineEntity fine2 = new FineEntity(null, customer, loan, "atraso", 1500, "pagada");
        FineEntity fine3 = new FineEntity(null, customer, loan, "daño leve", 3000, "no pagada");
        entityManager.persist(fine1);
        entityManager.persist(fine2);
        entityManager.persist(fine3);
        entityManager.flush();

        // when
        Optional<List<FineEntity>> foundFines = fineRepository.findFineByCustomerRutAndStatusAndType(
                "44444444-4", "no pagada", "atraso");

        // then
        assertThat(foundFines).isPresent();
        assertThat(foundFines.get()).hasSize(1);
        assertThat(foundFines.get().get(0).getStatus()).isEqualTo("no pagada");
        assertThat(foundFines.get().get(0).getType()).isEqualTo("atraso");
    }

    @Test
    public void whenFindCustomersWithOverdueLoansByDateRange_thenReturnCustomers() {
        // given
        CustomerEntity customer1 = new CustomerEntity(null, "Roberto Vega", "55555555-5",
                "roberto@email.com", "+56955555555", "ACTIVE");
        CustomerEntity customer2 = new CustomerEntity(null, "Sandra López", "66666666-6",
                "sandra@email.com", "+56966666666", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas de Medición");
        ToolEntity tool = new ToolEntity(null, "Nivel", category, "AVAILABLE");

        entityManager.persist(customer1);
        entityManager.persist(customer2);
        entityManager.persist(category);
        entityManager.persist(tool);

        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 30);

        LoanEntity loan1 = new LoanEntity(null, customer1, tool,
                LocalDate.of(2025, 11, 10), LocalDate.of(2025, 11, 15), "vencido", 5000);
        LoanEntity loan2 = new LoanEntity(null, customer2, tool,
                LocalDate.of(2025, 11, 20), LocalDate.of(2025, 11, 25), "vencido", 5000);

        entityManager.persist(loan1);
        entityManager.persist(loan2);

        FineEntity fine1 = new FineEntity(null, customer1, loan1, "atraso", 2000, "no pagada");
        FineEntity fine2 = new FineEntity(null, customer2, loan2, "atraso", 2500, "no pagada");
        entityManager.persist(fine1);
        entityManager.persist(fine2);
        entityManager.flush();

        // when
        List<CustomerEntity> customers = fineRepository.findCustomersWithOverdueLoansByDateRange(startDate, endDate);

        // then
        assertThat(customers).hasSize(2);
        assertThat(customers).extracting(CustomerEntity::getRut)
                .containsExactlyInAnyOrder("55555555-5", "66666666-6");
    }

    @Test
    public void whenFindCustomersWithOverdueLoans_thenReturnCustomers() {
        // given
        CustomerEntity customer = new CustomerEntity(null, "Diego Morales", "77777777-7",
                "diego@email.com", "+56977777777", "ACTIVE");
        CategoryEntity category = new CategoryEntity(null, "Herramientas Eléctricas");
        ToolEntity tool = new ToolEntity(null, "Taladro", category, "AVAILABLE");

        entityManager.persist(customer);
        entityManager.persist(category);
        entityManager.persist(tool);

        LoanEntity loan = new LoanEntity(null, customer, tool,
                LocalDate.now(), LocalDate.now().minusDays(1), "vencido", 5000);
        entityManager.persist(loan);

        FineEntity fine = new FineEntity(null, customer, loan, "atraso", 3000, "no pagada");
        entityManager.persistAndFlush(fine);

        // when
        List<CustomerEntity> customers = fineRepository.findCustomersWithOverdueLoans();

        // then
        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getRut()).isEqualTo("77777777-7");
    }
}