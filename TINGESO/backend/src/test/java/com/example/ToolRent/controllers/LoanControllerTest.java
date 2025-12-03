package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.entities.LoanEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.services.LoanService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = LoanController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    // Helper methods para crear objetos de prueba
    private CustomerEntity createCustomer(Long id, String name, String rut) {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(id);
        customer.setName(name);
        customer.setRut(rut);
        customer.setEmail(name.toLowerCase() + "@example.cl");
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

    // ==================== listAllLoans ====================

    @Test
    public void listAllLoans_ShouldReturnLoans() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "prestado");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "prestado");

        LoanEntity loan1 = new LoanEntity(
                1L, customer1, tool1,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        LoanEntity loan2 = new LoanEntity(
                2L, customer2, tool2,
                LocalDate.of(2024, 11, 3),
                LocalDate.of(2024, 11, 10),
                "activo", 42000);

        ArrayList<LoanEntity> loanList = new ArrayList<>(Arrays.asList(loan1, loan2));

        given(loanService.getLoans()).willReturn(loanList);

        mockMvc.perform(get("/api/v1/loans/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("activo")))
                .andExpect(jsonPath("$[1].status", is("activo")));
    }

    // ==================== findLoanById ====================

    @Test
    public void findLoanById_ShouldReturnLoan() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "prestado");

        LoanEntity loan = new LoanEntity(
                1L, customer, tool,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        given(loanService.findLoanById(1L)).willReturn(loan);

        mockMvc.perform(get("/api/v1/loans/id/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("activo")))
                .andExpect(jsonPath("$.loanValue", is(35000)));
    }

    @Test
    public void findLoanById_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.findLoanById(999L))
                .willThrow(new RuntimeException("Préstamo no encontrado"));

        mockMvc.perform(get("/api/v1/loans/id/{id}", 999L))
                .andExpect(status().isBadRequest());
    }

    // ==================== findLoanByStatus ====================

    @Test
    public void findLoanByStatus_ShouldReturnLoans() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "prestado");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "prestado");

        LoanEntity loan1 = new LoanEntity(
                1L, customer1, tool1,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        LoanEntity loan2 = new LoanEntity(
                2L, customer2, tool2,
                LocalDate.of(2024, 11, 3),
                LocalDate.of(2024, 11, 10),
                "activo", 42000);

        ArrayList<LoanEntity> activeLoans = new ArrayList<>(Arrays.asList(loan1, loan2));

        given(loanService.findLoanByStatus("activo")).willReturn(activeLoans);

        mockMvc.perform(get("/api/v1/loans/status/{status}", "activo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("activo")))
                .andExpect(jsonPath("$[1].status", is("activo")));
    }

    @Test
    public void findLoanByStatus_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.findLoanByStatus("invalid"))
                .willThrow(new RuntimeException("Estado inválido"));

        mockMvc.perform(get("/api/v1/loans/status/{status}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ==================== findLoanByCustomerRut ====================

    @Test
    public void findLoanByCustomerRut_ShouldReturnLoans() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "prestado");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "disponible");

        LoanEntity loan1 = new LoanEntity(
                1L, customer, tool1,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        LoanEntity loan2 = new LoanEntity(
                2L, customer, tool2,
                LocalDate.of(2024, 10, 15),
                LocalDate.of(2024, 10, 22),
                "finalizado", 42000);

        ArrayList<LoanEntity> customerLoans = new ArrayList<>(Arrays.asList(loan1, loan2));

        given(loanService.findLoanByCustomerRut("12345678-5")).willReturn(customerLoans);

        mockMvc.perform(get("/api/v1/loans/customerRut/{rut}", "12345678-5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void findLoanByCustomerRut_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.findLoanByCustomerRut("invalid-rut"))
                .willThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/loans/customerRut/{rut}", "invalid-rut"))
                .andExpect(status().isBadRequest());
    }

    // ==================== findLoanByCustomerRutAndStatus ====================

    @Test
    public void findByCustomerRutAndStatus_ShouldReturnLoans() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "prestado");

        LoanEntity loan = new LoanEntity(
                1L, customer, tool,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        ArrayList<LoanEntity> activeLoans = new ArrayList<>(Arrays.asList(loan));

        given(loanService.findByCustomerRutAndStatus("12345678-5", "activo")).willReturn(activeLoans);

        mockMvc.perform(get("/api/v1/loans/customerRut/status/{rut}/{status}", "12345678-5", "activo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("activo")));
    }

    @Test
    public void findByCustomerRutAndStatus_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.findByCustomerRutAndStatus("invalid-rut", "activo"))
                .willThrow(new RuntimeException("Error en la búsqueda"));

        mockMvc.perform(get("/api/v1/loans/customerRut/status/{rut}/{status}", "invalid-rut", "activo"))
                .andExpect(status().isBadRequest());
    }

    // ==================== findLoanByReturnDate ====================

    @Test
    public void findLoanByReturnDate_ShouldReturnLoans() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "prestado");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "prestado");

        LocalDate returnDate = LocalDate.of(2024, 11, 8);

        LoanEntity loan1 = new LoanEntity(
                1L, customer1, tool1,
                LocalDate.of(2024, 11, 1),
                returnDate,
                "activo", 35000);

        LoanEntity loan2 = new LoanEntity(
                2L, customer2, tool2,
                LocalDate.of(2024, 11, 1),
                returnDate,
                "activo", 42000);

        ArrayList<LoanEntity> loansForDate = new ArrayList<>(Arrays.asList(loan1, loan2));

        given(loanService.findLoanByReturnDate(returnDate)).willReturn(loansForDate);

        mockMvc.perform(get("/api/v1/loans/returnDate/{returnDate}", "2024-11-08"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void findLoanByReturnDate_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.findLoanByReturnDate(Mockito.any(LocalDate.class)))
                .willThrow(new RuntimeException("Error al buscar por fecha"));

        mockMvc.perform(get("/api/v1/loans/returnDate/{returnDate}", "2024-11-08"))
                .andExpect(status().isBadRequest());
    }

    // ==================== createLoan ====================

    @Test
    public void createLoan_ShouldReturnCreatedLoan() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LoanEntity createdLoan = new LoanEntity(
                1L, customer, tool,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        given(loanService.makeLoan(Mockito.any(LoanEntity.class))).willReturn(createdLoan);

        String loanJson = """
            {
                "customer": {
                    "id": 1,
                    "name": "Alex Garcia",
                    "rut": "12345678-5"
                },
                "tool": {
                    "id": 1,
                    "name": "Taladro"
                },
                "loanDate": "2024-11-01",
                "returnDate": "2024-11-08",
                "status": "activo",
                "loanValue": 35000
            }
            """;

        mockMvc.perform(post("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("activo")))
                .andExpect(jsonPath("$.loanValue", is(35000)));
    }

    @Test
    public void createLoan_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.makeLoan(Mockito.any(LoanEntity.class)))
                .willThrow(new RuntimeException("Herramienta no disponible"));

        String loanJson = """
            {
                "customer": {
                    "id": 1,
                    "name": "Alex Garcia",
                    "rut": "12345678-5"
                },
                "tool": {
                    "id": 999,
                    "name": "Invalid"
                },
                "loanDate": "2024-11-01",
                "returnDate": "2024-11-08",
                "status": "activo",
                "loanValue": 35000
            }
            """;

        mockMvc.perform(post("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== returnLoan ====================

    @Test
    public void returnLoan_ShouldReturnUpdatedLoan() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LoanEntity returnedLoan = new LoanEntity(
                1L, customer, tool,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "finalizado", 35000);

        given(loanService.loanReturn(1L, "bueno")).willReturn(returnedLoan);

        mockMvc.perform(put("/api/v1/loans/{id}/{toolReturnStatus}", 1L, "bueno"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("finalizado")));
    }

    @Test
    public void returnLoan_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.loanReturn(999L, "bueno"))
                .willThrow(new RuntimeException("Préstamo no encontrado"));

        mockMvc.perform(put("/api/v1/loans/{id}/{toolReturnStatus}", 999L, "bueno"))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTS PARA REPORTES (ÉPICA 6) ==========

    @Test
    public void getActiveLoans_ShouldReturnActiveLoans() throws Exception {
        CustomerEntity customer1 = createCustomer(1L, "Alex Garcia", "12345678-5");
        CustomerEntity customer2 = createCustomer(2L, "Maria Lopez", "98765432-1");

        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "prestado");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "prestado");

        LoanEntity loan1 = new LoanEntity(
                1L, customer1, tool1,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        LoanEntity loan2 = new LoanEntity(
                2L, customer2, tool2,
                LocalDate.of(2024, 11, 3),
                LocalDate.of(2024, 11, 10),
                "vencido", 42000);

        ArrayList<LoanEntity> activeLoans = new ArrayList<>(Arrays.asList(loan1, loan2));

        given(loanService.getActiveLoans(null, null)).willReturn(activeLoans);

        mockMvc.perform(get("/api/v1/loans/reports/active-loans"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getActiveLoans_WithDateRange_ShouldReturnFilteredLoans() throws Exception {
        CustomerEntity customer = createCustomer(1L, "Alex Garcia", "12345678-5");
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "prestado");

        LoanEntity loan = new LoanEntity(
                1L, customer, tool,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 8),
                "activo", 35000);

        ArrayList<LoanEntity> filteredLoans = new ArrayList<>(Arrays.asList(loan));

        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 30);

        given(loanService.getActiveLoans(startDate, endDate)).willReturn(filteredLoans);

        mockMvc.perform(get("/api/v1/loans/reports/active-loans")
                        .param("startDate", "2024-11-01")
                        .param("endDate", "2024-11-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getActiveLoans_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.getActiveLoans(null, null))
                .willThrow(new RuntimeException("Error al obtener préstamos activos"));

        mockMvc.perform(get("/api/v1/loans/reports/active-loans"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getMostRentedTools_ShouldReturnRanking() throws Exception {
        Object[] tool1Ranking = {"Taladro", "Electricas", 15L};
        Object[] tool2Ranking = {"Sierra", "Electricas", 12L};
        Object[] tool3Ranking = {"Martillo", "Manuales", 8L};

        ArrayList<Object[]> ranking = new ArrayList<>(Arrays.asList(tool1Ranking, tool2Ranking, tool3Ranking));

        given(loanService.getMostRentedTools(null, null)).willReturn(ranking);

        mockMvc.perform(get("/api/v1/loans/reports/most-rented-tools"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0][0]", is("Taladro")))
                .andExpect(jsonPath("$[0][1]", is("Electricas")))
                .andExpect(jsonPath("$[0][2]", is(15)));
    }

    @Test
    public void getMostRentedTools_WithDateRange_ShouldReturnFilteredRanking() throws Exception {
        Object[] tool1Ranking = {"Taladro", "Electricas", 10L};
        Object[] tool2Ranking = {"Sierra", "Electricas", 7L};

        ArrayList<Object[]> ranking = new ArrayList<>(Arrays.asList(tool1Ranking, tool2Ranking));

        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 30);

        given(loanService.getMostRentedTools(startDate, endDate)).willReturn(ranking);

        mockMvc.perform(get("/api/v1/loans/reports/most-rented-tools")
                        .param("startDate", "2024-11-01")
                        .param("endDate", "2024-11-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0][2]", is(10)));
    }

    @Test
    public void getMostRentedTools_ShouldReturnBadRequest_WhenException() throws Exception {
        given(loanService.getMostRentedTools(null, null))
                .willThrow(new RuntimeException("Error al generar ranking"));

        mockMvc.perform(get("/api/v1/loans/reports/most-rented-tools"))
                .andExpect(status().isBadRequest());
    }
}