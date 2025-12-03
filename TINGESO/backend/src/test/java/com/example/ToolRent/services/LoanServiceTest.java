package com.example.ToolRent.services;


import com.example.ToolRent.entities.*;
import com.example.ToolRent.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private FineRepository fineRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ToolService toolService;

    @Mock
    private GlobalRatesService globalRatesService;

    @Mock
    private FineService fineService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ToolsInventoryRepository toolsInventoryRepository;

    @Mock
    private KardexRepository kardexRepository;

    @Mock
    private KardexService kardexService;

    @InjectMocks
    private LoanService loanService;

    private LoanEntity loanEntity;
    private CustomerEntity customerEntity;
    private ToolEntity toolEntity;
    private CategoryEntity categoryEntity;
    private ToolsInventoryEntity toolsInventoryEntity;
    private FineEntity fineEntity;
    private GlobalRatesEntity globalRatesEntity;
    private KardexEntity kardexEntity;

    @BeforeEach
    void setUp() {
        // Setup CategoryEntity
        categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Electricidad");

        // Setup CustomerEntity
        customerEntity = new CustomerEntity();
        customerEntity.setId(1L);
        customerEntity.setRut("12345678-9");
        customerEntity.setName("Juan Pérez");
        customerEntity.setStatus("activo");

        // Setup ToolEntity
        toolEntity = new ToolEntity();
        toolEntity.setId(1L);
        toolEntity.setName("taladro");
        toolEntity.setStatus("disponible");
        toolEntity.setCategory(categoryEntity);

        // Setup ToolsInventoryEntity
        toolsInventoryEntity = new ToolsInventoryEntity();
        toolsInventoryEntity.setId(1L);
        toolsInventoryEntity.setName("taladro");
        toolsInventoryEntity.setCategory("Electricidad");
        toolsInventoryEntity.setTotalTools(5);
        toolsInventoryEntity.setCurrentStock(5);
        toolsInventoryEntity.setDailyRentalRate(1000);

        // Setup LoanEntity
        loanEntity = new LoanEntity();
        loanEntity.setId(1L);
        loanEntity.setCustomer(customerEntity);
        loanEntity.setTool(toolEntity);
        loanEntity.setLoanDate(LocalDate.now());
        loanEntity.setReturnDate(LocalDate.now().plusDays(5));
        loanEntity.setStatus("activo");
        loanEntity.setLoanValue(5000);

        // Setup FineEntity
        fineEntity = new FineEntity();
        fineEntity.setId(1L);
        fineEntity.setCustomer(customerEntity);
        fineEntity.setLoan(loanEntity);
        fineEntity.setFineValue(2000);
        fineEntity.setStatus("no pagada");

        // Setup GlobalRatesEntity
        globalRatesEntity = new GlobalRatesEntity();
        globalRatesEntity.setId(1L);
        globalRatesEntity.setRateName("tarifa diaria de multa");
        globalRatesEntity.setDailyRateValue(500);

        // Setup KardexEntity
        kardexEntity = new KardexEntity();
        kardexEntity.setId(1L);
        kardexEntity.setTool(toolEntity);
        kardexEntity.setType("préstamo");
        kardexEntity.setDate(LocalDate.now());
        kardexEntity.setAffectedAmount(1);
    }

    // ==================== METODOS AUXILIARES ====================

    // ==================== normalizeString ====================

    @Test
    void whenNormalizeStringWithValidString_thenReturnNormalizedString() {
        // When
        String result = loanService.normalizeString("  HELLO WORLD  ");

        // Then
        assertThat(result).isEqualTo("hello world");
    }

    @Test
    void whenNormalizeStringWithNull_thenReturnNull() {
        // When
        String result = loanService.normalizeString(null);

        // Then
        assertThat(result).isNull();
    }

    // ==================== countLoands ====================

    @Test
    void whenCountLoandsWithNonEmptyList_thenReturnCount() {
        // Given
        ArrayList<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        loans.add(loanEntity);

        // When
        int result = loanService.countLoands(loans);

        // Then
        assertThat(result).isEqualTo(2);
    }

    @Test
    void whenCountLoandsWithEmptyList_thenReturnZero() {
        // Given
        ArrayList<LoanEntity> loans = new ArrayList<>();

        // When
        int result = loanService.countLoands(loans);

        // Then
        assertThat(result).isEqualTo(0);
    }

    // ==================== isTheSameTool ====================

    @Test
    void whenIsTheSameToolWithSameTool_thenReturnTrue() {
        // Given
        ArrayList<LoanEntity> clientActiveLoans = new ArrayList<>();
        clientActiveLoans.add(loanEntity);

        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(toolsInventoryEntity);

        // When
        boolean result = loanService.isTheSameTool(clientActiveLoans, toolEntity);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void whenIsTheSameToolWithDifferentTool_thenReturnFalse() {
        // Given
        ToolEntity differentTool = new ToolEntity();
        differentTool.setId(2L);
        differentTool.setName("sierra");
        differentTool.setCategory(categoryEntity);

        ArrayList<LoanEntity> clientActiveLoans = new ArrayList<>();
        clientActiveLoans.add(loanEntity);

        when(toolsInventoryRepository.findByNameAndCategory("sierra", "Electricidad"))
                .thenReturn(null);
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(toolsInventoryEntity);

        // When
        boolean result = loanService.isTheSameTool(clientActiveLoans, differentTool);

        // Then
        assertThat(result).isFalse();
    }

    // ==================== METODOS GET/FIND ====================

    // ==================== findLoanById ====================

    @Test
    void whenFindLoanByIdExists_thenReturnLoan() {
        // Given
        when(loanRepository.findLoanById(1L)).thenReturn(Optional.of(loanEntity));

        // When
        LoanEntity result = loanService.findLoanById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void whenFindLoanByIdNotExists_thenThrowException() {
        // Given
        when(loanRepository.findLoanById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loanService.findLoanById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pestamos con el id 999 no encontrado");
    }

    // ==================== getLoans ====================

    @Test
    void whenGetLoans_thenReturnAllLoans() {
        // Given
        List<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        when(loanRepository.findAll()).thenReturn(loans);

        // When
        ArrayList<LoanEntity> result = loanService.getLoans();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    // ==================== findByCustomerRutAndStatus ====================

    @Test
    void whenFindByCustomerRutAndStatusExists_thenReturnLoans() {
        // Given
        List<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        when(loanRepository.findByCustomerRutAndStatus("12345678-9", "activo"))
                .thenReturn(Optional.of(loans));

        // When
        ArrayList<LoanEntity> result = loanService.findByCustomerRutAndStatus("12345678-9", "activo");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCustomer().getRut()).isEqualTo("12345678-9");
    }

    @Test
    void whenFindByCustomerRutAndStatusNotExists_thenThrowException() {
        // Given
        when(loanRepository.findByCustomerRutAndStatus("99999999-9", "activo"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loanService.findByCustomerRutAndStatus("99999999-9", "activo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pestamos con Cliente con rut 99999999-9y estadoactivo no encontrado");
    }

    // ==================== findLoanByStatus ====================

    @Test
    void whenFindLoanByStatusExists_thenReturnLoans() {
        // Given
        List<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        when(loanRepository.findByStatus("activo")).thenReturn(Optional.of(loans));

        // When
        ArrayList<LoanEntity> result = loanService.findLoanByStatus("activo");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getStatus()).isEqualTo("activo");
    }

    @Test
    void whenFindLoanByStatusNotExists_thenThrowException() {
        // Given
        when(loanRepository.findByStatus("inexistente")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loanService.findLoanByStatus("inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prestamos con estado inexistente no encontrados");
    }


    // ==================== findLoanByCustomerRut ====================

    @Test
    void whenFindLoanByCustomerRutExists_thenReturnLoans() {
        // Given
        List<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        when(loanRepository.findByCustomerRut("12345678-9")).thenReturn(Optional.of(loans));

        // When
        ArrayList<LoanEntity> result = loanService.findLoanByCustomerRut("12345678-9");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCustomer().getRut()).isEqualTo("12345678-9");
    }

    @Test
    void whenFindLoanByCustomerRutNotExists_thenThrowException() {
        // Given
        when(loanRepository.findByCustomerRut("99999999-9")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loanService.findLoanByCustomerRut("99999999-9"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pestamos con cliente con rut 99999999-9 no encontrados");
    }



    // ==================== findLoanByReturnDate ====================

    @Test
    void whenFindLoanByReturnDateExists_thenReturnLoans() {
        // Given
        LocalDate returnDate = LocalDate.now().plusDays(5);
        List<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        when(loanRepository.findByReturnDate(returnDate)).thenReturn(Optional.of(loans));

        // When
        ArrayList<LoanEntity> result = loanService.findLoanByReturnDate(returnDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getReturnDate()).isEqualTo(returnDate);
    }

    @Test
    void whenFindLoanByReturnDateNotExists_thenThrowException() {
        // Given
        LocalDate returnDate = LocalDate.now().plusDays(10);
        when(loanRepository.findByReturnDate(returnDate)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loanService.findLoanByReturnDate(returnDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prestamos con fecha de devolucion");
    }

    // ==================== makeLoan ====================

    @Test
    void whenMakeLoanWithValidData_thenReturnLoan() {
        // Given
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomer(customerEntity);
        newLoan.setTool(toolEntity);
        newLoan.setReturnDate(LocalDate.now().plusDays(5));

        when(customerService.isActive("activo")).thenReturn(true);
        when(loanRepository.findByCustomerRutAndStatus("12345678-9", "activo"))
                .thenReturn(Optional.of(new ArrayList<>()));
        when(toolService.hasAvailableStock("taladro", "Electricidad")).thenReturn(true);
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(toolsInventoryEntity);
        when(toolService.borrowedTool(1L)).thenReturn(toolEntity);
        when(kardexService.registerMovement("préstamo", toolEntity)).thenReturn(kardexEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(newLoan);

        // When
        LoanEntity result = loanService.makeLoan(newLoan);

        // Then
        assertThat(result).isNotNull();
        verify(loanRepository).save(any(LoanEntity.class));
        verify(kardexRepository).save(any(KardexEntity.class));
    }

    @Test
    void whenMakeLoanWithRestrictedCustomer_thenThrowException() {
        // Given
        customerEntity.setStatus("restringido");
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomer(customerEntity);
        newLoan.setTool(toolEntity);

        when(customerService.isActive("restringido")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> loanService.makeLoan(newLoan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El cliente tiene estado restringido");
    }

    @Test
    void whenMakeLoanWithFiveActiveLoans_thenThrowException() {
        // Given
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomer(customerEntity);
        newLoan.setTool(toolEntity);

        List<LoanEntity> fiveLoans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fiveLoans.add(new LoanEntity());
        }

        when(customerService.isActive("activo")).thenReturn(true);
        when(loanRepository.findByCustomerRutAndStatus("12345678-9", "activo"))
                .thenReturn(Optional.of(fiveLoans));

        // When & Then
        assertThatThrownBy(() -> loanService.makeLoan(newLoan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El cliente tiene 5 prestamos activos");
    }

    @Test
    void whenMakeLoanWithSameTool_thenThrowException() {
        // Given
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomer(customerEntity);
        newLoan.setTool(toolEntity);

        List<LoanEntity> existingLoans = new ArrayList<>();
        existingLoans.add(loanEntity);

        when(customerService.isActive("activo")).thenReturn(true);
        when(loanRepository.findByCustomerRutAndStatus("12345678-9", "activo"))
                .thenReturn(Optional.of(existingLoans))
                .thenReturn(Optional.of(existingLoans));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(toolsInventoryEntity);

        // When & Then
        assertThatThrownBy(() -> loanService.makeLoan(newLoan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El cliente ya posee un prestamo con la herramienta solicitada");
    }

    @Test
    void whenMakeLoanWithNoStock_thenThrowException() {
        // Given
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomer(customerEntity);
        newLoan.setTool(toolEntity);

        when(customerService.isActive("activo")).thenReturn(true);
        when(loanRepository.findByCustomerRutAndStatus("12345678-9", "activo"))
                .thenReturn(Optional.of(new ArrayList<>()))
                .thenReturn(Optional.of(new ArrayList<>()));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(toolsInventoryEntity);
        when(toolService.hasAvailableStock("taladro", "Electricidad")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> loanService.makeLoan(newLoan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El stock de la herramienta es insuficiente");
    }

    @Test
    void whenMakeLoanWithBorrowedTool_thenThrowException() {
        // Given
        toolEntity.setStatus("prestada");
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomer(customerEntity);
        newLoan.setTool(toolEntity);

        when(customerService.isActive("activo")).thenReturn(true);
        when(loanRepository.findByCustomerRutAndStatus("12345678-9", "activo"))
                .thenReturn(Optional.of(new ArrayList<>()))
                .thenReturn(Optional.of(new ArrayList<>()));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(toolsInventoryEntity);
        when(toolService.hasAvailableStock("taladro", "Electricidad")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> loanService.makeLoan(newLoan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La herramienta solicitada ya esta prestada");
    }

    @Test
    void whenMakeLoanWithInvalidReturnDate_thenThrowException() {
        // Given
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomer(customerEntity);
        newLoan.setTool(toolEntity);
        newLoan.setReturnDate(LocalDate.now().minusDays(1));

        when(customerService.isActive("activo")).thenReturn(true);
        when(loanRepository.findByCustomerRutAndStatus("12345678-9", "activo"))
                .thenReturn(Optional.of(new ArrayList<>()))
                .thenReturn(Optional.of(new ArrayList<>()));
        when(toolsInventoryRepository.findByNameAndCategory("taladro", "Electricidad"))
                .thenReturn(toolsInventoryEntity);
        when(toolService.hasAvailableStock("taladro", "Electricidad")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> loanService.makeLoan(newLoan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La fecha de devolución no puede ser anterior o igual a la fecha de prestamo");
    }

    // ==================== loanReturn ====================

    @Test
    void whenLoanReturnOnTimeWithGoodCondition_thenUpdateLoan() {
        // Given
        loanEntity.setReturnDate(LocalDate.now().plusDays(1));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(toolService.availableTool(1L)).thenReturn(toolEntity);
        when(kardexService.registerMovement("devolución", toolEntity)).thenReturn(kardexEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);

        // When
        LoanEntity result = loanService.loanReturn(1L, "Buen Estado");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("finalizado");
        verify(toolService).availableTool(1L);
        verify(kardexRepository).save(any(KardexEntity.class));
    }

    @Test
    void whenLoanReturnOnTimeWithDamagedCondition_thenUpdateLoanAndRestrictCustomer() {
        // Given
        loanEntity.setReturnDate(LocalDate.now().plusDays(1));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(toolService.damagedTool(1L)).thenReturn(toolEntity);
        when(kardexService.registerMovement("devolución", toolEntity)).thenReturn(kardexEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);

        // When
        LoanEntity result = loanService.loanReturn(1L, "Dañada");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("evaluación pendiente");
        verify(toolService).damagedTool(1L);
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    void whenLoanReturnLateWithGoodCondition_thenCreatePendingFine() {
        // Given
        loanEntity.setReturnDate(LocalDate.now().minusDays(1));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(toolService.availableTool(1L)).thenReturn(toolEntity);
        when(kardexService.registerMovement("devolución", toolEntity)).thenReturn(kardexEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);

        // When
        LoanEntity result = loanService.loanReturn(1L, "Buen Estado");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("multa pendiente");
        verify(toolService).availableTool(1L);
    }

    @Test
    void whenLoanReturnLateWithDamagedCondition_thenRestrictCustomer() {
        // Given
        loanEntity.setReturnDate(LocalDate.now().minusDays(1));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));
        when(toolService.damagedTool(1L)).thenReturn(toolEntity);
        when(kardexService.registerMovement("devolución", toolEntity)).thenReturn(kardexEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);

        // When
        LoanEntity result = loanService.loanReturn(1L, "Dañada");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("evaluación pendiente");
        verify(toolService).damagedTool(1L);
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    void whenLoanReturnWithInvalidLoanId_thenThrowException() {
        // Given
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loanService.loanReturn(999L, "Buen Estado"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Préstamo con ID 999 no encontrada");
    }

    @Test
    void whenLoanReturnWithInvalidStatus_thenThrowException() {
        // Given
        loanEntity.setStatus("finalizado");
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));

        // When & Then
        assertThatThrownBy(() -> loanService.loanReturn(1L, "Buen Estado"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado del préstamo no es válido. Solo se aceptan estados activos o vencidos para la devolución ");
    }

    @Test
    void whenLoanReturnWithInvalidToolStatus_thenThrowException() {
        // Given
        loanEntity.setReturnDate(LocalDate.now().plusDays(1));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loanEntity));

        // When & Then
        assertThatThrownBy(() -> loanService.loanReturn(1L, "Estado Inválido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado ingresado para la herramienta a devolver no es válido");
    }

    // ========== MÉTODOS PARA REPORTES (ÉPICA 6) ==========

    // ==================== getActiveLoans ====================
    @Test
    void whenGetActiveLoansWithDateRange_thenReturnLoans() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        List<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        when(loanRepository.findActiveLoansByDateRange(startDate, endDate)).thenReturn(loans);

        // When
        List<LoanEntity> result = loanService.getActiveLoans(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(loanRepository).findActiveLoansByDateRange(startDate, endDate);
    }

    @Test
    void whenGetActiveLoansWithoutDateRange_thenReturnAllActiveLoans() {
        // Given
        List<LoanEntity> loans = new ArrayList<>();
        loans.add(loanEntity);
        when(loanRepository.findAllActiveLoans()).thenReturn(loans);

        // When
        List<LoanEntity> result = loanService.getActiveLoans(null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(loanRepository).findAllActiveLoans();
    }

    // ==================== getMostRentedTools ====================

    @Test
    void whenGetMostRentedToolsWithDateRange_thenReturnRanking() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<Object[]> ranking = new ArrayList<>();
        Object[] toolData = new Object[]{"taladro", 10L};
        ranking.add(toolData);
        when(loanRepository.findMostRentedToolsByDateRange(startDate, endDate)).thenReturn(ranking);

        // When
        List<Object[]> result = loanService.getMostRentedTools(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(loanRepository).findMostRentedToolsByDateRange(startDate, endDate);
    }

    @Test
    void whenGetMostRentedToolsWithoutDateRange_thenReturnAllTimeRanking() {
        // Given
        List<Object[]> ranking = new ArrayList<>();
        Object[] toolData = new Object[]{"taladro", 25L};
        ranking.add(toolData);
        when(loanRepository.findMostRentedTools()).thenReturn(ranking);

        // When
        List<Object[]> result = loanService.getMostRentedTools(null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(loanRepository).findMostRentedTools();
    }

}
