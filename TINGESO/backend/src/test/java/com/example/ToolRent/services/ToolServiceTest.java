package com.example.ToolRent.services;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.KardexEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.entities.ToolsInventoryEntity;
import com.example.ToolRent.repositories.KardexRepository;
import com.example.ToolRent.repositories.ToolRepository;
import com.example.ToolRent.repositories.ToolsInventoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToolServiceTest {

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private ToolsInventoryRepository toolsInventoryRepository;

    @Mock
    private KardexRepository kardexRepository;

    @Mock
    private KardexService kardexService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ToolService toolService;

    private ToolEntity toolEntity;
    private ToolsInventoryEntity inventoryEntity;
    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Electricidad");

        toolEntity = new ToolEntity();
        toolEntity.setId(1L);
        toolEntity.setName("taladro");
        toolEntity.setCategory(categoryEntity);
        toolEntity.setStatus("disponible");

        inventoryEntity = new ToolsInventoryEntity();
        inventoryEntity.setId(1L);
        inventoryEntity.setName("taladro");
        inventoryEntity.setCategory("Electricidad");
        inventoryEntity.setTotalTools(10);
        inventoryEntity.setCurrentStock(8);
        inventoryEntity.setReplacementValue(50000);
        inventoryEntity.setDailyRentalRate(5000);
    }

    // ==================== TESTS FUNCIONES AUXILIARES ====================

    // ==================== normalizeString ====================

    @Test
    void whenNormalizeStringWithValidString_thenReturnString() {
        // Given
        String validString = "ACTivO";

        // When
        String result = ToolService.normalizeString(validString);

        // Then
        assertThat(result).isEqualTo("activo");
    }

    @Test
    void whenNormalizeStringWithNullString_thenReturnString() {
        // Given
        String validString = null;

        // When
        String result = ToolService.normalizeString(validString);

        // Then
        assertThat(result).isEqualTo(null);
    }

    // ==================== isToolAlreadyRegistered ====================

    @Test
    void whenToolExistsWithSameCategoryAndName_thenReturnTrue() {
        //Given
        when(toolsInventoryRepository.findByName("taladro")).thenReturn(inventoryEntity);

        //When
        boolean result = toolService.isToolAlreadyRegistered("taladro", "Electricidad");

        //Then
        assertThat(result).isTrue();
    }

    @Test
    void whenToolDoesNotExist_thenReturnFalse() {
        //Given
        when(toolsInventoryRepository.findByName("sierra")).thenReturn(null);

        //When
        boolean result = toolService.isToolAlreadyRegistered("sierra", "Carpintería");

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenToolExistsButDifferentCategory_thenReturnFalse() {
        //Given
        when(toolsInventoryRepository.findByName("taladro")).thenReturn(inventoryEntity);

        //When
        boolean result = toolService.isToolAlreadyRegistered("taladro", "Carpintería");

        //Then
        assertThat(result).isFalse();
    }

    // ==================== getToolInventory ====================

    @Test
    void whenToolInventoryExists_thenReturnInventory() {
        //Given
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);

        //When
        ToolsInventoryEntity result = toolService.getToolInventory("Taladro", "Electricidad");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("taladro");
        assertThat(result.getCategory()).isEqualTo("Electricidad");
    }

    @Test
    void whenToolInventoryDoesNotExist_thenThrowException() {
        //Given
        when(toolsInventoryRepository.findByNameAndCategory("sierra", "Carpintería"))
                .thenReturn(null);

        //When & Then
        assertThatThrownBy(() -> toolService.getToolInventory("sierra", "Carpintería"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Herramienta no encontrada en el inventario");
    }

    // ==================== validateReplacementValue y updateReplacementValue====================

    @Test
    void whenUpdateReplacementValueWithValidValue_thenUpdateSuccessfully() {
        //Given
        when(toolsInventoryRepository.findById(1L)).thenReturn(Optional.of(inventoryEntity));
        when(toolsInventoryRepository.save(any())).thenReturn(inventoryEntity);

        //When
        ToolsInventoryEntity result = toolService.updateReplacementValue(1L, 60000);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getReplacementValue()).isEqualTo(60000);
        verify(toolsInventoryRepository).save(inventoryEntity);
    }

    @Test
    void whenUpdateReplacementValueWithNullValue_thenThrowException() {
        //When y Then
        assertThatThrownBy(() -> toolService.updateReplacementValue(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de reposición no puede ser nulo");
    }

    @Test
    void whenUpdateReplacementValueWithNegativeValue_thenThrowException() {
        //When y Then
        assertThatThrownBy(() -> toolService.updateReplacementValue(1L, -1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de reposición no puede ser negativo");
    }

    @Test
    void whenUpdateReplacementValueBelowMinimum_thenThrowException() {
        //When y Then
        assertThatThrownBy(() -> toolService.updateReplacementValue(1L, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El valor de reposición debe ser mínimo");
    }

    @Test
    void whenUpdateReplacementValueAboveMaximum_thenThrowException() {
        //When y Then
        assertThatThrownBy(() -> toolService.updateReplacementValue(1L, 15000000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El valor de reposición no puede exceder");
    }

    // ==================== validateDailyRentalRate y updateDailyRentalRate ====================

    @Test
    void whenUpdateDailyRentalRateWithValidValue_thenUpdateSuccessfully() {
        //Given
        when(toolsInventoryRepository.findById(1L)).thenReturn(Optional.of(inventoryEntity));
        when(toolsInventoryRepository.save(any())).thenReturn(inventoryEntity);

        //When
        ToolsInventoryEntity result = toolService.updateDailyRentalRate(1L, 7000);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getDailyRentalRate()).isEqualTo(7000);
        verify(toolsInventoryRepository).save(inventoryEntity);
    }

    @Test
    void whenUpdateDailyRentalRateWithNullValue_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> toolService.updateDailyRentalRate(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta no puede ser nulo");
    }

    @Test
    void whenUpdateDailyRentalRateWithNegativeValue_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> toolService.updateDailyRentalRate(1L, -500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta no puede ser negativo");
    }

    @Test
    void whenUpdateDailyRentalRateBelowMinimum_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> toolService.updateDailyRentalRate(1L, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El valor de renta debe ser mínimo");
    }

    @Test
    void whenIpdateDailyRentalRateAboveMaximum_thenThrowException() {
        //When y Then
        assertThatThrownBy(() -> toolService.updateDailyRentalRate(1L, 15000000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El valor de renta no puede exceder");
    }

    // ==================== hasAvailableStock ====================

    @Test
    void whenToolHasAvailableStock_thenReturnTrue() {
        //Given
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);

        //When
        boolean result = toolService.hasAvailableStock("taladro", "Electricidad");

        //Then
        assertThat(result).isTrue();
    }

    @Test
    void whenToolHasNoStock_thenReturnFalse() {
        //Given
        inventoryEntity.setCurrentStock(0);
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);

        //When
        boolean result = toolService.hasAvailableStock("taladro", "Electricidad");

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenToolDoesNotExistInInventory_thenReturnFalse() {
        //Given
        when(toolsInventoryRepository.findByNameAndCategory("sierra", "Carpintería"))
                .thenReturn(null);

        //When
        boolean result = toolService.hasAvailableStock("sierra", "Carpintería");

        //Then
        assertThat(result).isFalse();
    }

    // ====================== FUNCIONES GET O FIND =====================


    // ==================== findToolById ====================

    @Test
    void whenFindToolByIdWithExistingId_thenReturnTool() {
        //Given
        when(toolRepository.findToolById(1L)).thenReturn(toolEntity);

        //When
        ToolEntity result = toolService.findToolById(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(toolRepository).findToolById(1L);
    }

    // ==================== findByName ====================
    @Test
    void whenFindByNameWithExistingTools_thenReturnListOfTools() {
        //Given
        List<ToolEntity> tools = new ArrayList<>();
        tools.add(toolEntity);
        when(toolRepository.findByName("taladro")).thenReturn(tools);

        //When
        ArrayList<ToolEntity> result = toolService.findByName("Taladro");

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("taladro");
    }

    @Test
    void whenFindByNameNormalizesInput_thenSearchWithLowercase() {
        //Given
        List<ToolEntity> tools = new ArrayList<>();
        tools.add(toolEntity);
        when(toolRepository.findByName("taladro")).thenReturn(tools);

        //When
        ArrayList<ToolEntity> result = toolService.findByName("TALADRO  ");

        //Then
        assertThat(result).isNotNull();
        verify(toolRepository).findByName("taladro");
    }

    // ==================== findToolByNameAndCategory ====================

    @Test
    void whenFindToolByNameAndCategoryExists_thenReturnTool() {
        //Given
        when(toolRepository.findToolByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(Optional.of(toolEntity));

        //When
        ToolEntity result = toolService.findToolByNameAndCategory("Taladro", "Electricidad");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("taladro");
    }

    @Test
    void whenFindToolByNameAndCategoryNotExists_thenReturnNull() {
        //Given
        when(toolRepository.findToolByNameAndCategory("sierra", "Carpintería"))
                .thenReturn(Optional.empty());

        //When
        ToolEntity result = toolService.findToolByNameAndCategory("sierra", "Carpintería");

        //Then
        assertThat(result).isNull();
    }

    // ==================== getTools ====================

    @Test
    void whenGetTools_thenReturnAllTools() {
        //Given
        List<ToolEntity> tools = new ArrayList<>();
        tools.add(toolEntity);
        when(toolRepository.findAll()).thenReturn(tools);

        //When
        ArrayList<ToolEntity> result = toolService.getTools();

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    // ==================== getToolsByStatus ====================

    @Test
    void whenGetToolsByStatusWithValidStatus_thenReturnTools() {
        //Given
        List<ToolEntity> tools = new ArrayList<>();
        tools.add(toolEntity);
        when(toolRepository.findByStatus("disponible")).thenReturn(tools);

        //When
        ArrayList<ToolEntity> result = toolService.getToolsByStatus("Disponible");

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    void whenGetToolsByStatusWithInvalidStatus_thenThrowException() {
        //Given
        String invalidStatus = "estado_invalido";

        //When & Then
        assertThatThrownBy(() -> toolService.getToolsByStatus(invalidStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado no es válido");
    }

    @Test
    void whenGetToolsByStatusNormalizesInput_thenSearchWithLowercase() {
        //Given
        List<ToolEntity> tools = new ArrayList<>();
        tools.add(toolEntity);
        when(toolRepository.findByStatus("prestada")).thenReturn(tools);

        //When
        ArrayList<ToolEntity> result = toolService.getToolsByStatus("PRESTADA  ");

        //Then
        assertThat(result).isNotNull();
        verify(toolRepository).findByStatus("prestada");
    }

    // ==================== getToolsByCategory ====================

    @Test
    void whenGetToolsByCategory_thenReturnTools() {
        //Given
        List<ToolEntity> tools = new ArrayList<>();
        tools.add(toolEntity);
        when(toolRepository.findByCategory("Electricidad")).thenReturn(tools);

        //When
        ArrayList<ToolEntity> result = toolService.getToolsByCategory("Electricidad");

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    // ==================== getToolsInventory ====================

    @Test
    void whenGetToolsInventory_thenReturnAllInventory() {
        //Given
        List<ToolsInventoryEntity> inventory = new ArrayList<>();
        inventory.add(inventoryEntity);
        when(toolsInventoryRepository.findAll()).thenReturn(inventory);

        //When
        ArrayList<ToolsInventoryEntity> result = toolService.getToolsInventory();

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    // ==================== findByCategory ====================

    @Test
    void whenFindInventoryByCategory_thenReturnInventoryList() {
        //Given
        List<ToolsInventoryEntity> inventory = new ArrayList<>();
        inventory.add(inventoryEntity);
        when(toolsInventoryRepository.findByCategory("Electricidad")).thenReturn(inventory);

        //When
        ArrayList<ToolsInventoryEntity> result = toolService.findByCategory("Electricidad");

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    // ==================== findToolByName ====================

    @Test
    void whenFindToolByNameInInventory_thenReturnInventory() {
        //Given
        when(toolsInventoryRepository.findByName("taladro")).thenReturn(inventoryEntity);

        //When
        ToolsInventoryEntity result = toolService.findToolByName("taladro");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("taladro");
    }


    // ==================== findById ====================

    @Test
    void whenFindInventoryByIdExists_thenReturnInventory() {
        //Given
        when(toolsInventoryRepository.findById(1L)).thenReturn(Optional.of(inventoryEntity));

        //When
        ToolsInventoryEntity result = toolService.findById(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void whenFindInventoryByIdNotExists_thenThrowException() {
        //Given
        when(toolsInventoryRepository.findById(99L)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> toolService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Inventario de herramienta con ID 99 no encontrada");
    }

    // ==================== GUARDAR HERRAMIENTAS ====================

    // ==================== saveRegisteredTool ====================

    @Test
    void whenSaveRegisteredToolWithValidData_thenSaveSuccessfully() {
        //Given
        when(categoryService.isCategoryExists("Electricidad")).thenReturn(true);
        when(toolsInventoryRepository.findByName("taladro")).thenReturn(inventoryEntity);
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);
        when(toolRepository.save(any())).thenReturn(toolEntity);
        when(kardexService.registerMovement(eq("ingreso"), any())).thenReturn(new KardexEntity());

        //When
        List<ToolEntity> result = toolService.saveRegisteredTool(toolEntity, 3);

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(inventoryEntity.getTotalTools()).isEqualTo(13);
        assertThat(inventoryEntity.getCurrentStock()).isEqualTo(11);
        verify(toolsInventoryRepository).save(inventoryEntity);
        verify(toolRepository, times(3)).save(any());
        verify(kardexRepository, times(3)).save(any());
    }

    @Test
    void whenSaveRegisteredToolWithInvalidQuantity_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> toolService.saveRegisteredTool(toolEntity, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La cantidad debe ser mayor a 0");
    }

    @Test
    void whenSaveRegisteredToolWithNonExistentCategory_thenThrowException() {
        //Given
        when(categoryService.isCategoryExists("Electricidad")).thenReturn(false);

        //When & Then
        assertThatThrownBy(() -> toolService.saveRegisteredTool(toolEntity, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre de la categoria no existe");
    }

    @Test
    void whenSaveRegisteredToolNotInSystem_thenThrowException() {
        //Given
        when(categoryService.isCategoryExists("Electricidad")).thenReturn(true);
        when(toolsInventoryRepository.findByName("taladro")).thenReturn(null);

        //When & Then
        assertThatThrownBy(() -> toolService.saveRegisteredTool(toolEntity, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La herramienta ingresada no se encuentra en el sistema");
    }

    // ==================== saveTool ====================

    @Test
    void whenSaveNewToolWithValidData_thenSaveSuccessfully() {
        //Given
        when(categoryService.isCategoryExists("Electricidad")).thenReturn(true);
        when(toolsInventoryRepository.findByName("taladro")).thenReturn(null);
        when(toolRepository.save(any())).thenReturn(toolEntity);
        when(kardexService.registerMovement(eq("ingreso"), any())).thenReturn(new KardexEntity());

        //When
        List<ToolEntity> result = toolService.saveTool(toolEntity, 2, 50000, 5000);

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(toolsInventoryRepository).save(any());
        verify(toolRepository, times(2)).save(any());
        verify(kardexRepository, times(2)).save(any());
    }

    @Test
    void whenSaveNewToolWithInvalidQuantity_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> toolService.saveTool(toolEntity, 0, 50000, 5000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La cantidad debe ser mayor a 0");
    }

    @Test
    void whenSaveToolAlreadyRegistered_thenThrowException() {
        //Given
        when(categoryService.isCategoryExists("Electricidad")).thenReturn(true);
        when(toolsInventoryRepository.findByName("taladro")).thenReturn(inventoryEntity);

        //When & Then
        assertThatThrownBy(() -> toolService.saveTool(toolEntity, 2, 50000, 5000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La herramienta ingresada SI se encuentra en el sistema");
    }

    @Test
    void whenSaveToolWithInvalidReplacementValue_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> toolService.saveTool(toolEntity, 2, 1000, 5000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El valor de reposición debe ser mínimo");
    }

    @Test
    void whenSaveToolWithInvalidDailyRentalRate_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> toolService.saveTool(toolEntity, 2, 50000, 500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El valor de renta debe ser mínimo");
    }

    @Test
    void whenSaveToolWithNonExistentCategory_thenThrowException() {
        //Given
        when(categoryService.isCategoryExists("Electricidad")).thenReturn(false);

        //When & Then
        assertThatThrownBy(() -> toolService.saveTool(toolEntity, 2, 50000, 5000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre de la categoria no existe");
    }

    // ==================== FUNCIONES USADOS EN OTROS SERVICIOS ====================

    // ==================== borrowedTool ====================

    @Test
    void whenBorrowedToolWithValidId_thenUpdateStatusAndDecreaseStock() {
        //Given
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);
        when(toolRepository.save(any())).thenReturn(toolEntity);

        //When
        ToolEntity result = toolService.borrowedTool(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("prestada");
        assertThat(inventoryEntity.getCurrentStock()).isEqualTo(7);
        verify(toolsInventoryRepository).save(inventoryEntity);
        verify(toolRepository).save(toolEntity);
    }

    @Test
    void whenBorrowedToolNotFound_thenThrowException() {
        //Given
        when(toolRepository.findById(99L)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> toolService.borrowedTool(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Herramienta con ID 99 no encontrada");
    }

    // ==================== availableTool ====================

    @Test
    void whenAvailableToolWithValidId_thenUpdateStatusAndIncreaseStock() {
        //Given
        toolEntity.setStatus("prestada");
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);
        when(toolRepository.save(any())).thenReturn(toolEntity);

        //When
        ToolEntity result = toolService.availableTool(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("disponible");
        assertThat(inventoryEntity.getCurrentStock()).isEqualTo(9);
        verify(toolsInventoryRepository).save(inventoryEntity);
        verify(toolRepository).save(toolEntity);
    }

    @Test
    void whenAvailableToolNotFound_thenThrowException() {
        //Given
        when(toolRepository.findById(99L)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> toolService.availableTool(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Herramienta con ID 99 no encontrada");
    }

    // ==================== decommissionedTool ====================

    @Test
    void whenDecommissionedToolWithValidId_thenUpdateStatusAndDecreaseTotalTools() {
        //Given
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);
        when(kardexService.registerMovement(eq("baja"), any())).thenReturn(new KardexEntity());

        //When
        ToolEntity result = toolService.decommissionedTool(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("dada de baja");
        assertThat(inventoryEntity.getTotalTools()).isEqualTo(9);
        verify(toolsInventoryRepository).save(inventoryEntity);
        verify(toolRepository).save(toolEntity);
        verify(kardexRepository).save(any());
    }

    @Test
    void whenDecommissionedToolNotFound_thenThrowException() {
        //Given
        when(toolRepository.findById(99L)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> toolService.decommissionedTool(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Herramienta con ID 99 no encontrada");
    }

    // ==================== damagedTool ====================

    @Test
    void whenDamagedToolWithValidId_thenUpdateStatusToRepair() {
        //Given
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));
        when(kardexService.registerMovement(eq("reparación"), any())).thenReturn(new KardexEntity());

        //When
        ToolEntity result = toolService.damagedTool(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("en reparacion");
        verify(toolRepository).save(toolEntity);
        verify(kardexRepository).save(any());
    }

    @Test
    void whenDamagedToolNotFound_thenThrowException() {
        //Given
        when(toolRepository.findById(99L)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> toolService.damagedTool(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Herramienta con ID 99 no encontrada");
    }

    // ==================== repairedTool ====================
    @Test
    void whenRepairedToolWithValidStatus_thenMakeToolAvailable() {
        //Given
        toolEntity.setStatus("en reparacion");
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(inventoryEntity);
        when(toolRepository.save(any())).thenReturn(toolEntity);

        //When
        ToolEntity result = toolService.repairedTool(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("disponible");
        verify(toolRepository, times(2)).save(any());
    }

    @Test
    void whenRepairedToolWithInvalidStatus_thenThrowException() {
        //Given
        toolEntity.setStatus("disponible");
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));

        //When & Then
        assertThatThrownBy(() -> toolService.repairedTool(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado de la herramiemnta no es correcto");
    }

    @Test
    void whenRepairedToolNotFound_thenThrowException() {
        //Given
        when(toolRepository.findById(99L)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> toolService.repairedTool(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Herramienta con ID 99 no encontrada");
    }

}
