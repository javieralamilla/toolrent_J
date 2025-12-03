package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CustomerEntity;
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
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void whenFindByRut_thenReturnCustomer() {
        // given
        CustomerEntity customer = new CustomerEntity(
                null,
                "Juan Pérez",
                "12345678-9",
                "juan.perez@email.com",
                "+56912345678",
                "ACTIVE");
        entityManager.persistAndFlush(customer);

        // when
        Optional<CustomerEntity> found = customerRepository.findByRut(customer.getRut());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getRut()).isEqualTo(customer.getRut());
    }

    @Test
    public void whenFindByStatus_thenReturnCustomers() {
        // given
        CustomerEntity customer1 = new CustomerEntity(
                null,
                "María González",
                "12345678-9",
                "maria.gonzalez@email.com",
                "+56912345678",
                "ACTIVE");
        CustomerEntity customer2 = new CustomerEntity(
                null,
                "Pedro Silva",
                "98765432-1",
                "pedro.silva@email.com",
                "+56987654321",
                "ACTIVE");
        entityManager.persist(customer1);
        entityManager.persist(customer2);
        entityManager.flush();

        // when
        List<CustomerEntity> foundCustomers = customerRepository.findByStatus("ACTIVE");

        // then
        assertThat(foundCustomers).hasSize(2).extracting(CustomerEntity::getStatus).containsOnly("ACTIVE");
    }

    @Test
    public void whenFindByEmail_thenReturnCustomer() {
        // given
        CustomerEntity customer = new CustomerEntity(
                null,
                "Ana Torres",
                "11111111-1",
                "ana.torres@email.com",
                "+56911111111",
                "ACTIVE");
        entityManager.persistAndFlush(customer);

        // when
        CustomerEntity found = customerRepository.findByEmail(customer.getEmail());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    public void whenFindByPhoneNumber_thenReturnCustomer() {
        // given
        CustomerEntity customer = new CustomerEntity(
                null,
                "Carlos Ramírez",
                "22222222-2",
                "carlos.ramirez@email.com",
                "+56922222222",
                "ACTIVE");
        entityManager.persistAndFlush(customer);

        // when
        CustomerEntity found = customerRepository.findByPhoneNumber(customer.getPhoneNumber());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
    }
}