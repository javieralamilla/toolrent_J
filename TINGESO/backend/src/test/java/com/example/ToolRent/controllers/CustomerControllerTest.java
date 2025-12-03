package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CustomerController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    // ==================== listCustomers ====================

    @Test
    public void listCustomers_ShouldReturnCustomers() throws Exception {
        CustomerEntity customer1 = new CustomerEntity(
                1L,
                "Alex Garcia",
                "12345678-5",
                "alex@example.cl",
                "987654321",
                "activo");

        CustomerEntity customer2 = new CustomerEntity(
                2L,
                "Jose Garcia",
                "19124070-7",
                "Jose@example.cl",
                "987634321",
                "activo");

        ArrayList<CustomerEntity> customerList = new ArrayList<>(Arrays.asList(customer1, customer2));

        given(customerService.getCustomers()).willReturn(customerList);

        mockMvc.perform(get("/api/v1/customers/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alex Garcia")))
                .andExpect(jsonPath("$[1].name", is("Jose Garcia")));
    }

    // ==================== getCustomerByRut ====================

    @Test
    public void getCustomerByRut_ShouldReturnCustomer() throws Exception {
        CustomerEntity customer = new CustomerEntity(
                1L,
                "Alex Garcia",
                "12345678-5",
                "alex@example.cl",
                "987654321",
                "activo");

        given(customerService.getCustomerByRut("12345678-5")).willReturn(customer);

        mockMvc.perform(get("/api/v1/customers/rut/{rut}", "12345678-5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Alex Garcia")))
                .andExpect(jsonPath("$.rut", is("12345678-5")));
    }

    @Test
    public void getCustomerByRut_ShouldReturnBadRequest_WhenException() throws Exception {
        given(customerService.getCustomerByRut("invalid-rut"))
                .willThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/customers/rut/{rut}", "invalid-rut"))
                .andExpect(status().isBadRequest());
    }

    // ==================== getCustomerById ====================

    @Test
    public void getCustomerById_ShouldReturnCustomer() throws Exception {
        CustomerEntity customer = new CustomerEntity(
                1L,
                "Alex Garcia",
                "12345678-5",
                "alex@example.cl",
                "987654321",
                "activo");

        given(customerService.getCustomerById(1L)).willReturn(customer);

        mockMvc.perform(get("/api/v1/customers/id/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Alex Garcia")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void getCustomerById_ShouldReturnBadRequest_WhenException() throws Exception {
        given(customerService.getCustomerById(999L))
                .willThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/customers/id/{id}", 999L))
                .andExpect(status().isBadRequest());
    }

    // ==================== listCustomersByStatus ====================

    @Test
    public void listCustomersByStatus_ShouldReturnCustomers() throws Exception {
        CustomerEntity customer1 = new CustomerEntity(
                1L,
                "Alex Garcia",
                "12345678-5",
                "alex@example.cl",
                "987654321",
                "activo");

        CustomerEntity customer2 = new CustomerEntity(
                2L,
                "Maria Lopez",
                "98765432-1",
                "maria@example.cl",
                "912345678",
                "activo");

        ArrayList<CustomerEntity> activeCustomers = new ArrayList<>(Arrays.asList(customer1, customer2));

        given(customerService.getCustomerByStatus("activo")).willReturn(activeCustomers);

        mockMvc.perform(get("/api/v1/customers/status/{status}", "activo"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("activo")))
                .andExpect(jsonPath("$[1].status", is("activo")));
    }

    // ==================== saveCustomer ====================

    @Test
    public void saveCustomer_ShouldReturnSavedCustomer() throws Exception {
        CustomerEntity savedCustomer = new CustomerEntity(
                1L,
                "Pedro Ramirez",
                "15678901-2",
                "pedro@example.cl",
                "956789012",
                "activo");

        given(customerService.saveCustomer(Mockito.any(CustomerEntity.class))).willReturn(savedCustomer);

        String customerJson = """
            {
                "name": "Pedro Ramirez",
                "rut": "15678901-2",
                "email": "pedro@example.cl",
                "phone": "956789012",
                "status": "activo"
            }
            """;

        mockMvc.perform(post("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Pedro Ramirez")))
                .andExpect(jsonPath("$.rut", is("15678901-2")))
                .andExpect(jsonPath("$.email", is("pedro@example.cl")));
    }

    @Test
    public void saveCustomer_ShouldReturnBadRequest_WhenException() throws Exception {
        given(customerService.saveCustomer(Mockito.any(CustomerEntity.class)))
                .willThrow(new RuntimeException("Error al guardar cliente"));

        String customerJson = """
            {
                "name": "Invalid Customer",
                "rut": "invalid",
                "email": "invalid@example.cl",
                "phone": "123",
                "status": "activo"
            }
            """;

        mockMvc.perform(post("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== updateCustomer ====================

    @Test
    public void updateCustomer_ShouldReturnUpdatedCustomer() throws Exception {
        CustomerEntity updatedCustomer = new CustomerEntity(
                1L,
                "Alex Garcia Updated",
                "12345678-5",
                "alex.updated@example.cl",
                "999888777",
                "activo");

        given(customerService.updateCustomer(Mockito.any(CustomerEntity.class))).willReturn(updatedCustomer);

        String customerJson = """
            {
                "id": 1,
                "name": "Alex Garcia Updated",
                "rut": "12345678-5",
                "email": "alex.updated@example.cl",
                "phone": "999888777",
                "status": "activo"
            }
            """;

        mockMvc.perform(put("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alex Garcia Updated")))
                .andExpect(jsonPath("$.email", is("alex.updated@example.cl")))
                .andExpect(jsonPath("$.phoneNumber", is("999888777")));
    }




    @Test
    public void updateCustomer_ShouldReturnBadRequest_WhenException() throws Exception {
        given(customerService.updateCustomer(Mockito.any(CustomerEntity.class)))
                .willThrow(new RuntimeException("Error al actualizar cliente"));

        String customerJson = """
            {
                "id": 999,
                "name": "Non Existent",
                "rut": "00000000-0",
                "email": "none@example.cl",
                "phone": "000000000",
                "status": "activo"
            }
            """;

        mockMvc.perform(put("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest());
    }
}