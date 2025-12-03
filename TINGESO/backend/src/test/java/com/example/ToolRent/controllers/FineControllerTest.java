package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.entities.FineEntity;
import com.example.ToolRent.entities.LoanEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.services.FineService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = FineController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class FineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FineService fineService;

    // Helper methods para crear objetos de prueba
    private CustomerEntity createCustomer(Long id, String name, String rut) {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(id);
        customer.setName(name);
        customer.setRut(rut);
        customer.setEmail(name.toLowerCase().replace(" ", "") + "@example.cl");
        customer.setPhoneNumber("987654321");
        customer.setStatus("activo");
        return customer;
    }

    private CategoryEntity createCategory(Long id, String name) {
        CategoryEntity category = new CategoryEntity();
        category.setId(id);
        category.setName(name);
        return category;
    }

    private ToolEntity createTool(Long id, String name, CategoryEntity category, String status) {
        ToolEntity tool = new ToolEntity();
        tool.setId(id);
        tool.setName(name);
        tool.setCategory(category);
        tool.setStatus(status);
        return tool;
    }

    private LoanEntity createLoan(Long id, CustomerEntity customer, ToolEntity tool, LocalDate loanDate, LocalDate returnDate, String status, int loanValue) {
        LoanEntity loan = new LoanEntity();
        loan.setId(id);
        loan.setCustomer(customer);
        loan.setTool(tool);
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setStatus(status);
        loan.setLoanValue(loanValue);
        return loan;
    }

    // ==================== listFines ====================

    @Test
    public void listFines_ShouldReturnFines() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "disponible");

        LoanEntity loan1 = createLoan(1L, customer1, tool1,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado", 35000);

        LoanEntity loan2 = createLoan(2L, customer2, tool2,
                LocalDate.of(2024, 10, 5),
                LocalDate.of(2024, 10, 12),
                "finalizado con multa", 42000);

        FineEntity fine1 = new FineEntity(1L, customer1, loan1, "atraso", 5000, "pagada");
        FineEntity fine2 = new FineEntity(2L, customer2, loan2, "daño leve", 10000, "no pagada");

        ArrayList<FineEntity> fineList = new ArrayList<>(Arrays.asList(fine1, fine2));

        given(fineService.getFines()).willReturn(fineList);

        mockMvc.perform(get("/api/v1/fines/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("atraso")))
                .andExpect(jsonPath("$[1].type", is("daño leve")));
    }

    // ==================== getFinesByStatus ====================

    @Test
    public void getFinesByStatus_ShouldReturnFines() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "disponible");

        LoanEntity loan1 = createLoan(1L, customer1, tool1,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        LoanEntity loan2 = createLoan(2L, customer2, tool2,
                LocalDate.of(2024, 10, 5),
                LocalDate.of(2024, 10, 12),
                "finalizado con multa", 42000);

        FineEntity fine1 = new FineEntity(1L, customer1, loan1, "atraso", 5000, "no pagada");
        FineEntity fine2 = new FineEntity(2L, customer2, loan2, "daño leve", 10000, "no pagada");

        ArrayList<FineEntity> unpaidFines = new ArrayList<>(Arrays.asList(fine1, fine2));

        given(fineService.getFinesByStatus("no pagada")).willReturn(unpaidFines);

        mockMvc.perform(get("/api/v1/fines/status/{status}", "no pagada"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("no pagada")))
                .andExpect(jsonPath("$[1].status", is("no pagada")));
    }

    @Test
    public void getFinesByStatus_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.getFinesByStatus("invalid"))
                .willThrow(new RuntimeException("Estado inválido"));

        mockMvc.perform(get("/api/v1/fines/status/{status}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ==================== getFinesByType ====================

    @Test
    public void getFinesByType_ShouldReturnFines() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "disponible");

        LoanEntity loan1 = createLoan(1L, customer1, tool1,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        LoanEntity loan2 = createLoan(2L, customer2, tool2,
                LocalDate.of(2024, 10, 5),
                LocalDate.of(2024, 10, 12),
                "finalizado con multa", 42000);

        FineEntity fine1 = new FineEntity(1L, customer1, loan1, "atraso", 5000, "no pagada");
        FineEntity fine2 = new FineEntity(2L, customer2, loan2, "atraso", 7000, "pagada");

        ArrayList<FineEntity> delayFines = new ArrayList<>(Arrays.asList(fine1, fine2));

        given(fineService.findByType("atraso")).willReturn(delayFines);

        mockMvc.perform(get("/api/v1/fines/type/{type}", "atraso"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("atraso")))
                .andExpect(jsonPath("$[1].type", is("atraso")));
    }

    @Test
    public void getFinesByType_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.findByType("invalid"))
                .willThrow(new RuntimeException("Tipo inválido"));

        mockMvc.perform(get("/api/v1/fines/type/{type}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ==================== findFineByCustomerRut ====================

    @Test
    public void findFineByCustomerRut_ShouldReturnFines() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "disponible");

        LoanEntity loan1 = createLoan(1L, customer, tool1,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        LoanEntity loan2 = createLoan(2L, customer, tool2,
                LocalDate.of(2024, 10, 5),
                LocalDate.of(2024, 10, 12),
                "finalizado con multa", 42000);

        FineEntity fine1 = new FineEntity(1L, customer, loan1, "atraso", 5000, "no pagada");
        FineEntity fine2 = new FineEntity(2L, customer, loan2, "daño leve", 10000, "pagada");

        ArrayList<FineEntity> customerFines = new ArrayList<>(Arrays.asList(fine1, fine2));

        given(fineService.findFineByCustomerRut("12345678-5")).willReturn(customerFines);

        mockMvc.perform(get("/api/v1/fines/customerRut/{customerRut}", "12345678-5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void findFineByCustomerRut_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.findFineByCustomerRut("invalid-rut"))
                .willThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/fines/customerRut/{customerRut}", "invalid-rut"))
                .andExpect(status().isBadRequest());
    }

    // ==================== findFineByCustomerRutAndStatus ====================

    @Test
    public void findFineByCustomerRutAndStatus_ShouldReturnFines() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LoanEntity loan = createLoan(1L, customer, tool,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        FineEntity fine = new FineEntity(1L, customer, loan, "atraso", 5000, "no pagada");

        ArrayList<FineEntity> unpaidCustomerFines = new ArrayList<>(Arrays.asList(fine));

        given(fineService.findFineByCustomerRutAndStatus("12345678-5", "no pagada")).willReturn(unpaidCustomerFines);

        mockMvc.perform(get("/api/v1/fines/customerRut/status/{customerRut}/{status}", "12345678-5", "no pagada"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("no pagada")));
    }

    @Test
    public void findFineByCustomerRutAndStatus_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.findFineByCustomerRutAndStatus("invalid-rut", "no pagada"))
                .willThrow(new RuntimeException("Error en la búsqueda"));

        mockMvc.perform(get("/api/v1/fines/customerRut/status/{customerRut}/{status}", "invalid-rut", "no pagada"))
                .andExpect(status().isBadRequest());
    }

    // ==================== findFineByCustomerRutAndType ====================

    @Test
    public void findFineByCustomerRutAndType_ShouldReturnFines() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LoanEntity loan = createLoan(1L, customer, tool,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        FineEntity fine = new FineEntity(1L, customer, loan, "atraso", 5000, "no pagada");

        ArrayList<FineEntity> delayFines = new ArrayList<>(Arrays.asList(fine));

        given(fineService.findFineByCustomerRutAndType("12345678-5", "atraso")).willReturn(delayFines);

        mockMvc.perform(get("/api/v1/fines/customerRut/type/{customerRut}/{type}", "12345678-5", "atraso"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("atraso")));
    }

    @Test
    public void findFineByCustomerRutAndType_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.findFineByCustomerRutAndType("invalid-rut", "atraso"))
                .willThrow(new RuntimeException("Error en la búsqueda"));

        mockMvc.perform(get("/api/v1/fines/customerRut/type/{customerRut}/{type}", "invalid-rut", "atraso"))
                .andExpect(status().isBadRequest());
    }

    // ==================== findFineByCustomerRutAndStatusAndType ====================

    @Test
    public void findFineByCustomerRutAndStatusAndType_ShouldReturnFines() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LoanEntity loan = createLoan(1L, customer, tool,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        FineEntity fine = new FineEntity(1L, customer, loan, "atraso", 5000, "no pagada");

        ArrayList<FineEntity> specificFines = new ArrayList<>(Arrays.asList(fine));

        given(fineService.findFineByCustomerRutAndStatusAndType("12345678-5", "no pagada", "atraso")).willReturn(specificFines);

        mockMvc.perform(get("/api/v1/fines/customerRut/status/type/{customerRut}/{status}/{type}",
                        "12345678-5", "no pagada", "atraso"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("no pagada")))
                .andExpect(jsonPath("$[0].type", is("atraso")));
    }

    @Test
    public void findFineByCustomerRutAndStatusAndType_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.findFineByCustomerRutAndStatusAndType("invalid-rut", "no pagada", "atraso"))
                .willThrow(new RuntimeException("Error en la búsqueda"));

        mockMvc.perform(get("/api/v1/fines/customerRut/status/type/{customerRut}/{status}/{type}",
                        "invalid-rut", "no pagada", "atraso"))
                .andExpect(status().isBadRequest());
    }

    // ==================== createFineForIrreparableDamage ====================

    @Test
    public void createFineForIrreparableDamage_ShouldReturnCreatedFine() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "en reparacion");

        LoanEntity loan = createLoan(1L, customer, tool,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        FineEntity createdFine = new FineEntity(1L, customer, loan, "daño irreparable", 50000, "no pagada");

        given(fineService.generateFineForIrreparableDamage(Mockito.any(CustomerEntity.class), Mockito.eq(1L)))
                .willReturn(createdFine);

        String customerJson = """
            {
                "id": 1,
                "name": "Alex Garcia",
                "rut": "12345678-5",
                "email": "alex@example.cl",
                "phone": "987654321",
                "status": "activo"
            }
            """;

        mockMvc.perform(post("/api/v1/fines/{idLoan}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is("daño irreparable")))
                .andExpect(jsonPath("$.fineValue", is(50000)))
                .andExpect(jsonPath("$.status", is("no pagada")));
    }

    @Test
    public void createFineForIrreparableDamage_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.generateFineForIrreparableDamage(Mockito.any(CustomerEntity.class), Mockito.eq(999L)))
                .willThrow(new RuntimeException("Préstamo no encontrado"));

        String customerJson = """
            {
                "id": 1,
                "name": "Alex Garcia",
                "rut": "12345678-5",
                "email": "alex@example.cl",
                "phone": "987654321",
                "status": "activo"
            }
            """;

        mockMvc.perform(post("/api/v1/fines/{idLoan}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== createFineForMinorDamage ====================

    @Test
    public void createFineForMinorDamage_ShouldReturnCreatedFine() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "en reparacion");

        LoanEntity loan = createLoan(1L, customer, tool,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado con multa", 35000);

        FineEntity createdFine = new FineEntity(1L, customer, loan, "daño leve", 15000, "no pagada");

        given(fineService.generateFineForMinorDamage(Mockito.any(CustomerEntity.class), Mockito.eq(1L), Mockito.eq(15000)))
                .willReturn(createdFine);

        String customerJson = """
            {
                "id": 1,
                "name": "Alex Garcia",
                "rut": "12345678-5",
                "email": "alex@example.cl",
                "phone": "987654321",
                "status": "activo"
            }
            """;

        mockMvc.perform(post("/api/v1/fines/minorDamage/{idLoan}/{fineValue}", 1L, 15000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is("daño leve")))
                .andExpect(jsonPath("$.fineValue", is(15000)))
                .andExpect(jsonPath("$.status", is("no pagada")));
    }

    @Test
    public void createFineForMinorDamage_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.generateFineForMinorDamage(Mockito.any(CustomerEntity.class), Mockito.eq(999L), Mockito.anyInt()))
                .willThrow(new RuntimeException("Préstamo no encontrado"));

        String customerJson = """
            {
                "id": 1,
                "name": "Alex Garcia",
                "rut": "12345678-5",
                "email": "alex@example.cl",
                "phone": "987654321",
                "status": "activo"
            }
            """;

        mockMvc.perform(post("/api/v1/fines/minorDamage/{idLoan}/{fineValue}", 999L, 15000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== payFine ====================

    @Test
    public void payFine_ShouldReturnPaidFine() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LoanEntity loan = createLoan(1L, customer, tool,
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 10, 8),
                "finalizado", 35000);

        FineEntity paidFine = new FineEntity(1L, customer, loan, "atraso", 5000, "pagada");

        given(fineService.payFine(Mockito.any(FineEntity.class))).willReturn(paidFine);

        String fineJson = """
            {
                "id": 1,
                "customer": {
                    "id": 1,
                    "name": "Alex Garcia",
                    "rut": "12345678-5"
                },
                "loan": {
                    "id": 1
                },
                "type": "atraso",
                "fineValue": 5000,
                "status": "pagada"
            }
            """;

        mockMvc.perform(put("/api/v1/fines/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fineJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("pagada")));
    }

    @Test
    public void payFine_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.payFine(Mockito.any(FineEntity.class)))
                .willThrow(new RuntimeException("Multa no encontrada"));

        String fineJson = """
            {
                "id": 999,
                "customer": {
                    "id": 1,
                    "name": "Alex Garcia",
                    "rut": "12345678-5"
                },
                "loan": {
                    "id": 1
                },
                "type": "atraso",
                "fineValue": 5000,
                "status": "pagada"
            }
            """;

        mockMvc.perform(put("/api/v1/fines/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fineJson))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTS PARA REPORTES (ÉPICA 6) ==========

    @Test
    public void getCustomersWithOverdue_ShouldReturnCustomers() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        ArrayList<CustomerEntity> customersWithOverdue = new ArrayList<>(Arrays.asList(customer1, customer2));

        given(fineService.getCustomersWithOverdue(null, null)).willReturn(customersWithOverdue);

        mockMvc.perform(get("/api/v1/fines/reports/customers-with-overdue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alex Garcia")))
                .andExpect(jsonPath("$[1].name", is("Maria Lopez")));
    }

    @Test
    public void getCustomersWithOverdue_WithDateRange_ShouldReturnFilteredCustomers() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        ArrayList<CustomerEntity> customersWithOverdue = new ArrayList<>(Arrays.asList(customer));

        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 30);

        given(fineService.getCustomersWithOverdue(startDate, endDate)).willReturn(customersWithOverdue);

        mockMvc.perform(get("/api/v1/fines/reports/customers-with-overdue")
                        .param("startDate", "2024-11-01")
                        .param("endDate", "2024-11-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alex Garcia")));
    }

    @Test
    public void getCustomersWithOverdue_ShouldReturnBadRequest_WhenException() throws Exception {
        given(fineService.getCustomersWithOverdue(null, null))
                .willThrow(new RuntimeException("Error al obtener clientes con atrasos"));

        mockMvc.perform(get("/api/v1/fines/reports/customers-with-overdue"))
                .andExpect(status().isBadRequest());
    }
}