package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.KardexEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.services.KardexService;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(value = KardexController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class KardexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KardexService kardexService;

    // Helper methods para crear objetos de prueba
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

    // ==================== listMoves ====================

    @Test
    public void listMoves_ShouldReturnAllMovements() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "prestado");

        KardexEntity movement1 = new KardexEntity(
                1L,
                "ingreso",
                LocalDate.of(2024, 11, 1),
                "admin",
                tool1,
                5);

        KardexEntity movement2 = new KardexEntity(
                2L,
                "prestamo",
                LocalDate.of(2024, 11, 3),
                "empleado1",
                tool2,
                1);

        ArrayList<KardexEntity> movements = new ArrayList<>(Arrays.asList(movement1, movement2));

        given(kardexService.getAllMoves()).willReturn(movements);

        mockMvc.perform(get("/api/v1/movements/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("ingreso")))
                .andExpect(jsonPath("$[1].type", is("prestamo")))
                .andExpect(jsonPath("$[0].affectedAmount", is(5)))
                .andExpect(jsonPath("$[1].affectedAmount", is(1)));
    }

    // ==================== getToolMovementHistory ====================

    @Test
    public void getToolMovementHistory_ShouldReturnMovements() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        KardexEntity movement1 = new KardexEntity(
                1L,
                "ingreso",
                LocalDate.of(2024, 11, 1),
                "admin",
                tool,
                5);

        KardexEntity movement2 = new KardexEntity(
                2L,
                "prestamo",
                LocalDate.of(2024, 11, 3),
                "empleado1",
                tool,
                1);

        KardexEntity movement3 = new KardexEntity(
                3L,
                "devolucion",
                LocalDate.of(2024, 11, 10),
                "empleado1",
                tool,
                1);

        ArrayList<KardexEntity> toolMovements = new ArrayList<>(Arrays.asList(movement1, movement2, movement3));

        given(kardexService.getToolMovementHistory(1L)).willReturn(toolMovements);

        mockMvc.perform(get("/api/v1/movements/toolId/{toolId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].type", is("ingreso")))
                .andExpect(jsonPath("$[1].type", is("prestamo")))
                .andExpect(jsonPath("$[2].type", is("devolucion")));
    }

    @Test
    public void getToolMovementHistory_ShouldReturnBadRequest_WhenException() throws Exception {
        given(kardexService.getToolMovementHistory(999L))
                .willThrow(new RuntimeException("Herramienta no encontrada"));

        mockMvc.perform(get("/api/v1/movements/toolId/{toolId}", 999L))
                .andExpect(status().isBadRequest());
    }

    // ==================== getMovementsByDateRange ====================

    @Test
    public void getMovementsByDateRange_ShouldReturnFilteredMovements() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "prestado");

        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 15);

        KardexEntity movement1 = new KardexEntity(
                1L,
                "ingreso",
                LocalDate.of(2024, 11, 5),
                "admin",
                tool1,
                3);

        KardexEntity movement2 = new KardexEntity(
                2L,
                "prestamo",
                LocalDate.of(2024, 11, 8),
                "empleado1",
                tool2,
                1);

        ArrayList<KardexEntity> filteredMovements = new ArrayList<>(Arrays.asList(movement1, movement2));

        given(kardexService.getMovementsByDateRange(startDate, endDate)).willReturn(filteredMovements);

        mockMvc.perform(get("/api/v1/movements/dateRange/{startDate}/{endDate}", "2024-11-01", "2024-11-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].date", is("2024-11-05")))
                .andExpect(jsonPath("$[1].date", is("2024-11-08")));
    }

    @Test
    public void getMovementsByDateRange_ShouldReturnBadRequest_WhenException() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 11, 15);
        LocalDate endDate = LocalDate.of(2024, 11, 1);

        given(kardexService.getMovementsByDateRange(startDate, endDate))
                .willThrow(new RuntimeException("Rango de fechas inválido"));

        mockMvc.perform(get("/api/v1/movements/dateRange/{startDate}/{endDate}", "2024-11-15", "2024-11-01"))
                .andExpect(status().isBadRequest());
    }

    // ==================== getToolMovementsByDateRange ====================

    @Test
    public void getToolMovementsByDateRange_ShouldReturnFilteredMovements() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 15);

        KardexEntity movement1 = new KardexEntity(
                1L,
                "ingreso",
                LocalDate.of(2024, 11, 2),
                "admin",
                tool,
                5);

        KardexEntity movement2 = new KardexEntity(
                2L,
                "prestamo",
                LocalDate.of(2024, 11, 5),
                "empleado1",
                tool,
                1);

        KardexEntity movement3 = new KardexEntity(
                3L,
                "devolucion",
                LocalDate.of(2024, 11, 12),
                "empleado1",
                tool,
                1);

        ArrayList<KardexEntity> toolMovements = new ArrayList<>(Arrays.asList(movement1, movement2, movement3));

        given(kardexService.getToolMovementsByDateRange(1L, startDate, endDate)).willReturn(toolMovements);

        mockMvc.perform(get("/api/v1/movements/tool/dateRange/{id}/{startDate}/{endDate}", 1L, "2024-11-01", "2024-11-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].type", is("ingreso")))
                .andExpect(jsonPath("$[1].type", is("prestamo")))
                .andExpect(jsonPath("$[2].type", is("devolucion")))
                .andExpect(jsonPath("$[0].date", is("2024-11-02")))
                .andExpect(jsonPath("$[1].date", is("2024-11-05")))
                .andExpect(jsonPath("$[2].date", is("2024-11-12")));
    }

    @Test
    public void getToolMovementsByDateRange_ShouldReturnBadRequest_WhenException() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 15);

        given(kardexService.getToolMovementsByDateRange(999L, startDate, endDate))
                .willThrow(new RuntimeException("Herramienta no encontrada"));

        mockMvc.perform(get("/api/v1/movements/tool/dateRange/{id}/{startDate}/{endDate}", 999L, "2024-11-01", "2024-11-15"))
                .andExpect(status().isBadRequest());
    }

    // ==================== getToolMovementHistory ====================

    @Test
    public void getToolMovementHistory_WithMultipleMovementTypes_ShouldReturnAllTypes() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        KardexEntity ingreso = new KardexEntity(
                1L, "ingreso", LocalDate.of(2024, 11, 1), "admin", tool, 10);

        KardexEntity prestamo = new KardexEntity(
                2L, "prestamo", LocalDate.of(2024, 11, 3), "empleado1", tool, 1);

        KardexEntity devolucion = new KardexEntity(
                3L, "devolucion", LocalDate.of(2024, 11, 10), "empleado1", tool, 1);

        KardexEntity baja = new KardexEntity(
                4L, "baja", LocalDate.of(2024, 11, 15), "admin", tool, 2);

        KardexEntity reparacion = new KardexEntity(
                5L, "reparacion", LocalDate.of(2024, 11, 20), "tecnico", tool, 1);

        ArrayList<KardexEntity> allMovementTypes = new ArrayList<>(
                Arrays.asList(ingreso, prestamo, devolucion, baja, reparacion));

        given(kardexService.getToolMovementHistory(1L)).willReturn(allMovementTypes);

        mockMvc.perform(get("/api/v1/movements/toolId/{toolId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].type", is("ingreso")))
                .andExpect(jsonPath("$[1].type", is("prestamo")))
                .andExpect(jsonPath("$[2].type", is("devolucion")))
                .andExpect(jsonPath("$[3].type", is("baja")))
                .andExpect(jsonPath("$[4].type", is("reparacion")));
    }

    @Test
    public void getMovementsByDateRange_WithEmptyResult_ShouldReturnEmptyList() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        ArrayList<KardexEntity> emptyMovements = new ArrayList<>();

        given(kardexService.getMovementsByDateRange(startDate, endDate)).willReturn(emptyMovements);

        mockMvc.perform(get("/api/v1/movements/dateRange/{startDate}/{endDate}", "2024-12-01", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    public void listMoves_WithDifferentUsers_ShouldReturnAllMovements() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = createTool(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = createTool(2L, "Sierra", category, "prestado");

        KardexEntity adminMovement = new KardexEntity(
                1L, "ingreso", LocalDate.of(2024, 11, 1), "admin", tool1, 10);

        KardexEntity employee1Movement = new KardexEntity(
                2L, "prestamo", LocalDate.of(2024, 11, 3), "empleado1", tool1, 1);

        KardexEntity employee2Movement = new KardexEntity(
                3L, "prestamo", LocalDate.of(2024, 11, 5), "empleado2", tool2, 1);

        KardexEntity tecnicoMovement = new KardexEntity(
                4L, "reparacion", LocalDate.of(2024, 11, 8), "tecnico", tool1, 1);

        ArrayList<KardexEntity> movements = new ArrayList<>(
                Arrays.asList(adminMovement, employee1Movement, employee2Movement, tecnicoMovement));

        given(kardexService.getAllMoves()).willReturn(movements);

        mockMvc.perform(get("/api/v1/movements/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].username", is("admin")))
                .andExpect(jsonPath("$[1].username", is("empleado1")))
                .andExpect(jsonPath("$[2].username", is("empleado2")))
                .andExpect(jsonPath("$[3].username", is("tecnico")));
    }

    @Test
    public void getToolMovementsByDateRange_WithSingleDayRange_ShouldReturnMovements() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = createTool(1L, "Taladro", category, "disponible");

        LocalDate singleDate = LocalDate.of(2024, 11, 5);

        KardexEntity movement1 = new KardexEntity(
                1L, "prestamo", singleDate, "empleado1", tool, 1);

        KardexEntity movement2 = new KardexEntity(
                2L, "devolucion", singleDate, "empleado2", tool, 1);

        ArrayList<KardexEntity> singleDayMovements = new ArrayList<>(Arrays.asList(movement1, movement2));

        given(kardexService.getToolMovementsByDateRange(1L, singleDate, singleDate)).willReturn(singleDayMovements);

        mockMvc.perform(get("/api/v1/movements/tool/dateRange/{id}/{startDate}/{endDate}",
                        1L, "2024-11-05", "2024-11-05"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].date", is("2024-11-05")))
                .andExpect(jsonPath("$[1].date", is("2024-11-05")));
    }
}