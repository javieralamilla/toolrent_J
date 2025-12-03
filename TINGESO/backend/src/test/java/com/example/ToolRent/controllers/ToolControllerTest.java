package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.entities.ToolsInventoryEntity;
import com.example.ToolRent.services.ToolService;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ToolController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class ToolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ToolService toolService;

    // Helper method para crear CategoryEntity
    private CategoryEntity createCategory(Long id, String name) {
        CategoryEntity category = new CategoryEntity();
        category.setId(id);
        category.setName(name);
        return category;
    }

    // ==================== findByToolId ====================

    @Test
    public void findByToolId_ShouldReturnTool() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = new ToolEntity(1L, "Taladro", category, "disponible");

        given(toolService.findToolById(1L)).willReturn(tool);

        mockMvc.perform(get("/api/v1/tools/id/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Taladro")))
                .andExpect(jsonPath("$.status", is("disponible")));
    }

    // ==================== findByName ====================

    @Test
    public void findByName_ShouldReturnTools() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = new ToolEntity(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = new ToolEntity(2L, "Taladro", category, "prestado");

        ArrayList<ToolEntity> tools = new ArrayList<>(Arrays.asList(tool1, tool2));

        given(toolService.findByName("Taladro")).willReturn(tools);

        mockMvc.perform(get("/api/v1/tools/name/{name}", "Taladro"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Taladro")))
                .andExpect(jsonPath("$[1].name", is("Taladro")));
    }

    // ==================== findToolByNameAndCategory ====================

    @Test
    public void findToolByNameAndCategory_ShouldReturnTool() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool = new ToolEntity(1L, "Taladro", category, "disponible");

        given(toolService.findToolByNameAndCategory("Taladro", "Electricas")).willReturn(tool);

        mockMvc.perform(get("/api/v1/tools/name/category/{name}/{category}", "Taladro", "Electricas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Taladro")));
    }

    // ==================== listAllTools ====================

    @Test
    public void listAllTools_ShouldReturnTools() throws Exception {
        CategoryEntity category1 = createCategory(1L, "Electricas");
        CategoryEntity category2 = createCategory(2L, "Manuales");

        ToolEntity tool1 = new ToolEntity(1L, "Taladro", category1, "disponible");
        ToolEntity tool2 = new ToolEntity(2L, "Martillo", category2, "disponible");

        ArrayList<ToolEntity> tools = new ArrayList<>(Arrays.asList(tool1, tool2));

        given(toolService.getTools()).willReturn(tools);

        mockMvc.perform(get("/api/v1/tools/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Taladro")))
                .andExpect(jsonPath("$[1].name", is("Martillo")));
    }

    // ==================== getToolsByStatus ====================

    @Test
    public void getToolsByStatus_ShouldReturnTools() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = new ToolEntity(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = new ToolEntity(2L, "Sierra", category, "disponible");

        ArrayList<ToolEntity> tools = new ArrayList<>(Arrays.asList(tool1, tool2));

        given(toolService.getToolsByStatus("disponible")).willReturn(tools);

        mockMvc.perform(get("/api/v1/tools/status/{status}", "disponible"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("disponible")))
                .andExpect(jsonPath("$[1].status", is("disponible")));
    }

    @Test
    public void getToolsByStatus_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.getToolsByStatus("invalid"))
                .willThrow(new RuntimeException("Estado inválido"));

        mockMvc.perform(get("/api/v1/tools/status/{status}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    // ==================== getToolsByCategory ====================

    @Test
    public void getToolsByCategory_ShouldReturnTools() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = new ToolEntity(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = new ToolEntity(2L, "Sierra", category, "disponible");

        ArrayList<ToolEntity> tools = new ArrayList<>(Arrays.asList(tool1, tool2));

        given(toolService.getToolsByCategory("Electricas")).willReturn(tools);

        mockMvc.perform(get("/api/v1/tools/category/{category}", "Electricas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ==================== getToolsInventory ====================

    @Test
    public void getToolsInventory_ShouldReturnInventory() throws Exception {
        ToolsInventoryEntity inventory1 = new ToolsInventoryEntity(
                1L, "Taladro", "Electricas", 10, 8, 50000, 5000);
        ToolsInventoryEntity inventory2 = new ToolsInventoryEntity(
                2L, "Martillo", "Manuales", 15, 12, 15000, 2000);

        ArrayList<ToolsInventoryEntity> inventoryList = new ArrayList<>(Arrays.asList(inventory1, inventory2));

        given(toolService.getToolsInventory()).willReturn(inventoryList);

        mockMvc.perform(get("/api/v1/tools/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Taladro")))
                .andExpect(jsonPath("$[1].name", is("Martillo")));
    }

    // ==================== findByCategory ====================

    @Test
    public void findByCategory_ShouldReturnInventoryByCategory() throws Exception {
        ToolsInventoryEntity inventory1 = new ToolsInventoryEntity(
                1L, "Taladro", "Electricas", 10, 8, 50000, 5000);
        ToolsInventoryEntity inventory2 = new ToolsInventoryEntity(
                2L, "Sierra", "Electricas", 5, 4, 60000, 6000);

        ArrayList<ToolsInventoryEntity> inventoryList = new ArrayList<>(Arrays.asList(inventory1, inventory2));

        given(toolService.findByCategory("Electricas")).willReturn(inventoryList);

        mockMvc.perform(get("/api/v1/tools/inventory/category/{category}", "Electricas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category", is("Electricas")))
                .andExpect(jsonPath("$[1].category", is("Electricas")));
    }

    // ==================== findToolByName ====================

    @Test
    public void findToolByName_ShouldReturnInventory() throws Exception {
        ToolsInventoryEntity inventory = new ToolsInventoryEntity(
                1L, "Taladro", "Electricas", 10, 8, 50000, 5000);

        given(toolService.findToolByName("Taladro")).willReturn(inventory);

        mockMvc.perform(get("/api/v1/tools/inventory/name/{name}", "Taladro"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Taladro")))
                .andExpect(jsonPath("$.totalTools", is(10)))
                .andExpect(jsonPath("$.currentStock", is(8)));
    }

    // ==================== findById ====================

    @Test
    public void findById_ShouldReturnInventory() throws Exception {
        ToolsInventoryEntity inventory = new ToolsInventoryEntity(
                1L, "Taladro", "Electricas", 10, 8, 50000, 5000);

        given(toolService.findById(1L)).willReturn(inventory);

        mockMvc.perform(get("/api/v1/tools/inventory/id/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Taladro")));
    }

    @Test
    public void findById_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.findById(999L))
                .willThrow(new RuntimeException("Inventario no encontrado"));

        mockMvc.perform(get("/api/v1/tools/inventory/id/{id}", 999L))
                .andExpect(status().isBadRequest());
    }

    // ==================== getToolInventory ====================

    @Test
    public void getToolInventory_ShouldReturnInventory() throws Exception {
        ToolsInventoryEntity inventory = new ToolsInventoryEntity(
                1L, "Taladro", "Electricas", 10, 8, 50000, 5000);

        given(toolService.getToolInventory("Taladro", "Electricas")).willReturn(inventory);

        mockMvc.perform(get("/api/v1/tools/inventory/name/category/{name}/{category}", "Taladro", "Electricas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Taladro")))
                .andExpect(jsonPath("$.category", is("Electricas")));
    }

    @Test
    public void getToolInventory_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.getToolInventory("Invalid", "Invalid"))
                .willThrow(new RuntimeException("Inventario no encontrado"));

        mockMvc.perform(get("/api/v1/tools/inventory/name/category/{name}/{category}", "Invalid", "Invalid"))
                .andExpect(status().isBadRequest());
    }

    // ==================== updateReplacementValue ====================

    @Test
    public void updateReplacementValue_ShouldReturnUpdatedInventory() throws Exception {
        ToolsInventoryEntity updatedInventory = new ToolsInventoryEntity(
                1L, "Taladro", "Electricas", 10, 8, 55000, 5000);

        given(toolService.updateReplacementValue(1L, 55000)).willReturn(updatedInventory);

        mockMvc.perform(put("/api/v1/tools/replacementValue/{id}/{value}", 1L, 55000))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.replacementValue", is(55000)));
    }

    @Test
    public void updateReplacementValue_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.updateReplacementValue(999L, 55000))
                .willThrow(new RuntimeException("Inventario no encontrado"));

        mockMvc.perform(put("/api/v1/tools/replacementValue/{id}/{value}", 999L, 55000))
                .andExpect(status().isBadRequest());
    }

    // ==================== updateDailyRentalRate ====================

    @Test
    public void updateDailyRentalRate_ShouldReturnUpdatedInventory() throws Exception {
        ToolsInventoryEntity updatedInventory = new ToolsInventoryEntity(
                1L, "Taladro", "Electricas", 10, 8, 50000, 6000);

        given(toolService.updateDailyRentalRate(1L, 6000)).willReturn(updatedInventory);

        mockMvc.perform(put("/api/v1/tools/dailyRentalRate/{id}/{dailyRentalRate}", 1L, 6000))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dailyRentalRate", is(6000)));
    }

    @Test
    public void updateDailyRentalRate_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.updateDailyRentalRate(999L, 6000))
                .willThrow(new RuntimeException("Inventario no encontrado"));

        mockMvc.perform(put("/api/v1/tools/dailyRentalRate/{id}/{dailyRentalRate}", 999L, 6000))
                .andExpect(status().isBadRequest());
    }

    // ==================== saveTool ====================

    @Test
    public void saveTool_ShouldReturnSavedTools() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = new ToolEntity(1L, "Taladro", category, "disponible");
        ToolEntity tool2 = new ToolEntity(2L, "Taladro", category, "disponible");

        ArrayList<ToolEntity> savedTools = new ArrayList<>(Arrays.asList(tool1, tool2));

        given(toolService.saveTool(Mockito.any(ToolEntity.class), Mockito.eq(2), Mockito.eq(50000), Mockito.eq(5000)))
                .willReturn(savedTools);

        String toolJson = """
            {
                "name": "Taladro",
                "category": {
                    "id": 1,
                    "name": "Electricas"
                },
                "status": "disponible"
            }
            """;

        mockMvc.perform(post("/api/v1/tools/{quantity}/{replacementValue}/{dailyRentalRate}", 2, 50000, 5000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toolJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Taladro")));
    }

    @Test
    public void saveTool_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.saveTool(Mockito.any(ToolEntity.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .willThrow(new RuntimeException("Error al guardar herramienta"));

        String toolJson = """
            {
                "name": "Invalid",
                "category": {
                    "id": 999,
                    "name": "Invalid"
                },
                "status": "disponible"
            }
            """;

        mockMvc.perform(post("/api/v1/tools/{quantity}/{replacementValue}/{dailyRentalRate}", 2, 50000, 5000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toolJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== saveRegisteredTool ====================

    @Test
    public void saveRegisteredTool_ShouldReturnSavedTools() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity tool1 = new ToolEntity(3L, "Taladro", category, "disponible");
        ToolEntity tool2 = new ToolEntity(4L, "Taladro", category, "disponible");

        ArrayList<ToolEntity> savedTools = new ArrayList<>(Arrays.asList(tool1, tool2));

        given(toolService.saveRegisteredTool(Mockito.any(ToolEntity.class), Mockito.eq(2)))
                .willReturn(savedTools);

        String toolJson = """
            {
                "name": "Taladro",
                "category": {
                    "id": 1,
                    "name": "Electricas"
                },
                "status": "disponible"
            }
            """;

        mockMvc.perform(post("/api/v1/tools/existing/{quantity}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toolJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Taladro")));
    }

    @Test
    public void saveRegisteredTool_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.saveRegisteredTool(Mockito.any(ToolEntity.class), Mockito.anyInt()))
                .willThrow(new RuntimeException("Herramienta no está registrada"));

        String toolJson = """
            {
                "name": "Unregistered",
                "category": {
                    "id": 1,
                    "name": "Electricas"
                },
                "status": "disponible"
            }
            """;

        mockMvc.perform(post("/api/v1/tools/existing/{quantity}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toolJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== repairedTool ====================

    @Test
    public void repairedTool_ShouldReturnRepairedTool() throws Exception {
        CategoryEntity category = createCategory(1L, "Electricas");
        ToolEntity repairedTool = new ToolEntity(1L, "Taladro", category, "disponible");

        given(toolService.repairedTool(1L)).willReturn(repairedTool);

        mockMvc.perform(put("/api/v1/tools/repairedTool/{toolId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("disponible")));
    }

    @Test
    public void repairedTool_ShouldReturnBadRequest_WhenException() throws Exception {
        given(toolService.repairedTool(999L))
                .willThrow(new RuntimeException("Herramienta no encontrada"));

        mockMvc.perform(put("/api/v1/tools/repairedTool/{toolId}", 999L))
                .andExpect(status().isBadRequest());
    }
}