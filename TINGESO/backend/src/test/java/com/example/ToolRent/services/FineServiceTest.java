package com.example.ToolRent.services;


import com.example.ToolRent.entities.*;
import com.example.ToolRent.repositories.*;
import org.assertj.core.api.Assertions;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FineServiceTest {

    @Mock
    private FineRepository fineRepository;

    @Mock
    private ToolsInventoryRepository toolsInventoryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanService loanService;

    @Mock
    private ToolService toolService;

    @Mock
    private ToolRepository toolRepository;

    @InjectMocks
    private FineService fineService;

    private FineEntity fineEntity;
    private CustomerEntity customerEntity;
    private LoanEntity loanEntity;
    private ToolEntity toolEntity;
    private CategoryEntity categoryEntity;
    private ToolsInventoryEntity inventoryEntity;

    @BeforeEach
    void setUp() {
        // Setup Customer
        customerEntity = new CustomerEntity();
        customerEntity.setId(1L);
        customerEntity.setRut("12345678-9");
        customerEntity.setName("Juan Perez");
        customerEntity.setStatus("activo");

        // Setup Category
        categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Herramientas Eléctricas");

        // Setup Tool
        toolEntity = new ToolEntity();
        toolEntity.setId(1L);
        toolEntity.setName("Taladro");
        toolEntity.setStatus("disponible");
        toolEntity.setCategory(categoryEntity);

        // Setup Loan
        loanEntity = new LoanEntity();
        loanEntity.setId(1L);
        loanEntity.setCustomer(customerEntity);
        loanEntity.setTool(toolEntity);
        loanEntity.setStatus("activo");
        loanEntity.setLoanDate(LocalDate.now().minusDays(5));
        loanEntity.setReturnDate(LocalDate.now().minusDays(1));

        // Setup Fine
        fineEntity = new FineEntity();
        fineEntity.setId(1L);
        fineEntity.setCustomer(customerEntity);
        fineEntity.setLoan(loanEntity);
        fineEntity.setType("atraso");
        fineEntity.setFineValue(5000);
        fineEntity.setStatus("no pagada");

        // Setup Inventory
        inventoryEntity = new ToolsInventoryEntity();
        inventoryEntity.setId(1L);
        inventoryEntity.setName("Taladro");
        inventoryEntity.setCategory(String.valueOf(categoryEntity));
        inventoryEntity.setReplacementValue(50000);
    }

    // ====================== FUNCIONES GET O FIND =====================


    // ==================== getFines ====================

    @Test
    void whenGetFines_thenReturnAllFines() {
        //Given
        List<FineEntity> fines = new ArrayList<>();
        fines.add(fineEntity);
        when(fineRepository.findAll()).thenReturn(fines);

        //When
        ArrayList<FineEntity> result = fineService.getFines();

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(fineRepository).findAll();
    }

    @Test
    void whenGetFinesWithEmptyList_thenReturnEmptyList() {
        //Given
        when(fineRepository.findAll()).thenReturn(new ArrayList<>());

        //When
        ArrayList<FineEntity> result = fineService.getFines();

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
        verify(fineRepository).findAll();
    }

    // ==================== getFinesByStatus ====================

    @Test
    void whenGetFinesByStatusWithValidStatus_thenReturnFines() {
        //Given
        List<FineEntity> fines = new ArrayList<>();
        fines.add(fineEntity);
        when(fineRepository.findByStatus("no pagada")).thenReturn(Optional.of(fines));

        //When
        ArrayList<FineEntity> result = fineService.getFinesByStatus("no pagada");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getStatus()).isEqualTo("no pagada");
    }

    @Test
    void whenGetFinesByStatusWithInvalidStatus_thenThrowException() {
        //Given
        String status = "estado_invalido";
        when(fineRepository.findByStatus(status)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> fineService.getFinesByStatus(status))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multas con estado " + status + " no encontrados");
    }

    // ==================== findFineByCustomerRut ====================

    @Test
    void whenFindFineByCustomerRutWithValidRut_thenReturnFines() {
        //Given
        List<FineEntity> fines = new ArrayList<>();
        fines.add(fineEntity);
        when(fineRepository.findFineByCustomerRut("12345678-9")).thenReturn(Optional.of(fines));

        //When
        ArrayList<FineEntity> result = fineService.findFineByCustomerRut("12345678-9");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCustomer().getRut()).isEqualTo("12345678-9");
    }

    @Test
    void whenFindFineByCustomerRutWithInvalidRut_thenThrowException() {
        //Given
        String rut = "00000000-0";
        when(fineRepository.findFineByCustomerRut(rut)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> fineService.findFineByCustomerRut(rut))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multas del cliente con rut " + rut + " no encontrados");
    }

    // ==================== findByType ====================

    @Test
    void whenFindByTypeWithValidType_thenReturnFines() {
        //Given
        List<FineEntity> fines = new ArrayList<>();
        fines.add(fineEntity);
        when(fineRepository.findByType("atraso")).thenReturn(Optional.of(fines));

        //When
        ArrayList<FineEntity> result = fineService.findByType("atraso");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getType()).isEqualTo("atraso");
    }

    @Test
    void whenFindByTypeWithInvalidType_thenThrowException() {
        //Given
        String type = "tipo_invalido";
        when(fineRepository.findByType(type)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> fineService.findByType(type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multas del cliente con tipo " + type + " no encontrados");
    }

    // ==================== findFineByCustomerRutAndStatus ====================

    @Test
    void whenFindFineByCustomerRutAndStatusWithValidData_thenReturnFines() {
        //Given
        List<FineEntity> fines = new ArrayList<>();
        fines.add(fineEntity);
        when(fineRepository.findFineByCustomerRutAndStatus("12345678-9", "no pagada"))
                .thenReturn(Optional.of(fines));

        //When
        ArrayList<FineEntity> result = fineService.findFineByCustomerRutAndStatus("12345678-9", "no pagada");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCustomer().getRut()).isEqualTo("12345678-9");
        assertThat(result.get(0).getStatus()).isEqualTo("no pagada");
    }

    @Test
    void whenFindFineByCustomerRutAndStatusWithInvalidData_thenThrowException() {
        //Given
        String rut = "00000000-0";
        String status = "pagada";
        when(fineRepository.findFineByCustomerRutAndStatus(rut, status))
                .thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> fineService.findFineByCustomerRutAndStatus(rut, status))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multas del cliente con rut " + rut + "y estado" + status + "no encontrados");
    }

    // ==================== findFineByCustomerRutAndType ====================

    @Test
    void whenFindFineByCustomerRutAndTypeWithValidData_thenReturnFines() {
        //Given
        List<FineEntity> fines = new ArrayList<>();
        fines.add(fineEntity);
        when(fineRepository.findFineByCustomerRutAndType("12345678-9", "atraso"))
                .thenReturn(Optional.of(fines));

        //When
        ArrayList<FineEntity> result = fineService.findFineByCustomerRutAndType("12345678-9", "atraso");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCustomer().getRut()).isEqualTo("12345678-9");
        assertThat(result.get(0).getType()).isEqualTo("atraso");
    }

    @Test
    void whenFindFineByCustomerRutAndTypeWithInvalidData_thenThrowException() {
        //Given
        String rut = "00000000-0";
        String type = "daño leve";
        when(fineRepository.findFineByCustomerRutAndType(rut, type))
                .thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> fineService.findFineByCustomerRutAndType(rut, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multas del cliente con rut " + rut + "y tipo" + type + "no encontrados");
    }


    // ==================== findFineByCustomerRutAndStatusAndType ====================

    @Test
    void whenFindFineByCustomerRutAndStatusAndTypeWithValidData_thenReturnFines() {
        //Given
        List<FineEntity> fines = new ArrayList<>();
        fines.add(fineEntity);
        when(fineRepository.findFineByCustomerRutAndStatusAndType("12345678-9", "no pagada", "atraso"))
                .thenReturn(Optional.of(fines));

        //When
        ArrayList<FineEntity> result = fineService.findFineByCustomerRutAndStatusAndType("12345678-9", "no pagada", "atraso");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCustomer().getRut()).isEqualTo("12345678-9");
        assertThat(result.get(0).getStatus()).isEqualTo("no pagada");
        assertThat(result.get(0).getType()).isEqualTo("atraso");
    }

    @Test
    void whenFindFineByCustomerRutAndStatusAndTypeWithInvalidData_thenThrowException() {
        //Given
        String rut = "00000000-0";
        String status = "pagada";
        String type = "daño leve";
        when(fineRepository.findFineByCustomerRutAndStatusAndType(rut, status, type))
                .thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> fineService.findFineByCustomerRutAndStatusAndType(rut, status, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multas del cliente con rut " + rut + ", estado" + status + "y tipo" + type + "no encontrados");
    }


    // ==================== getCustomersWithOverdue ====================

    @Test
    void whenGetCustomersWithOverdueWithDateRange_thenReturnCustomers() {
        //Given
        List<CustomerEntity> customers = new ArrayList<>();
        customers.add(customerEntity);
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        when(fineRepository.findCustomersWithOverdueLoansByDateRange(startDate, endDate))
                .thenReturn(customers);

        //When
        List<CustomerEntity> result = fineService.getCustomersWithOverdue(startDate, endDate);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(fineRepository).findCustomersWithOverdueLoansByDateRange(startDate, endDate);
    }

    @Test
    void whenGetCustomersWithOverdueWithoutDateRange_thenReturnAllCustomers() {
        //Given
        List<CustomerEntity> customers = new ArrayList<>();
        customers.add(customerEntity);
        when(fineRepository.findCustomersWithOverdueLoans()).thenReturn(customers);

        //When
        List<CustomerEntity> result = fineService.getCustomersWithOverdue(null, null);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(fineRepository).findCustomersWithOverdueLoans();
    }


    // ==================== GENERAR MULTAS ====================

    // ==================== generateALateFine ====================

    @Test
    void whenGenerateALateFine_thenReturnCreatedFine() {
        //Given
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.generateALateFine(customerEntity, loanEntity, 5000);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("atraso");
        assertThat(result.getFineValue()).isEqualTo(5000);
        assertThat(result.getStatus()).isEqualTo("no pagada");
        verify(fineRepository).save(any(FineEntity.class));
    }


    // ==================== generateFineForIrreparableDamage ====================

    @Test
    void whenGenerateFineForIrreparableDamageWithValidLoan_thenReturnFine() {
        //Given
        loanEntity.setStatus("evaluación pendiente");
        when(loanService.findLoanById(1L)).thenReturn(loanEntity);
        when(toolsInventoryRepository.findByNameAndCategory("Taladro", "Herramientas Eléctricas"))
                .thenReturn(inventoryEntity);
        when(toolService.decommissionedTool(1L)).thenReturn(toolEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.generateFineForIrreparableDamage(customerEntity, 1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("daño irreparable");
        assertThat(result.getFineValue()).isEqualTo(50000);
        assertThat(result.getStatus()).isEqualTo("no pagada");
        verify(loanRepository).save(any(LoanEntity.class));
        verify(toolService).decommissionedTool(1L);
    }

    @Test
    void whenGenerateFineForIrreparableDamageWithInvalidLoanStatus_thenThrowException() {
        //Given
        loanEntity.setStatus("activo");
        when(loanService.findLoanById(1L)).thenReturn(loanEntity);

        //When & Then
        assertThatThrownBy(() -> fineService.generateFineForIrreparableDamage(customerEntity, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado del prestamo no es correcto");
    }

    // ==================== generateFineForMinorDamage ====================

    @Test
    void whenGenerateFineForMinorDamageWithValidLoan_thenReturnFine() {
        //Given
        loanEntity.setStatus("evaluación pendiente");
        when(loanService.findLoanById(1L)).thenReturn(loanEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.generateFineForMinorDamage(customerEntity, 1L, 3000);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("daño leve");
        assertThat(result.getFineValue()).isEqualTo(3000);
        assertThat(result.getStatus()).isEqualTo("no pagada");
        verify(loanRepository).save(any(LoanEntity.class));
    }

    @Test
    void whenGenerateFineForMinorDamageWithInvalidLoanStatus_thenThrowException() {
        //Given
        loanEntity.setStatus("activo");
        when(loanService.findLoanById(1L)).thenReturn(loanEntity);

        //When & Then
        assertThatThrownBy(() -> fineService.generateFineForMinorDamage(customerEntity, 1L, 3000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado del prestamo no es correcto");
    }


    // ==================== OTROS METODOS ====================

    // ==================== calculateLatePaymentPenalty ====================

    @Test
    void whenCalculateLatePaymentPenaltyWithLateDays_thenReturnCorrectAmount() {
        //Given
        LocalDate returnDate = LocalDate.now().minusDays(5);
        LocalDate today = LocalDate.now();
        int dailyRate = 1000;

        //When
        int result = fineService.calculateLatePaymentPenalty(returnDate, today, dailyRate);

        //Then
        assertThat(result).isEqualTo(5000);
    }

    @Test
    void whenCalculateLatePaymentPenaltyWithNoLateDays_thenReturnZero() {
        //Given
        LocalDate returnDate = LocalDate.now();
        LocalDate today = LocalDate.now();
        int dailyRate = 1000;

        //When
        int result = fineService.calculateLatePaymentPenalty(returnDate, today, dailyRate);

        //Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    void whenCalculateLatePaymentPenaltyWithFutureDate_thenReturnZero() {
        //Given
        LocalDate returnDate = LocalDate.now().plusDays(5);
        LocalDate today = LocalDate.now();
        int dailyRate = 1000;

        //When
        int result = fineService.calculateLatePaymentPenalty(returnDate, today, dailyRate);

        //Then
        assertThat(result).isEqualTo(0);
    }


    // ==================== payFine ====================

    @Test
    void whenPayFineWithLoanInEvaluacionPendiente_thenOnlyUpdateFineStatus() {
        //Given
        loanEntity.setStatus("evaluación pendiente");
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.payFine(fineEntity);

        //Then
        assertThat(result.getStatus()).isEqualTo("pagada");
        verify(fineRepository).save(any(FineEntity.class));
        verify(loanRepository, never()).save(any(LoanEntity.class));
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    void whenPayFineWithAnotherFineForSameLoan_thenOnlyUpdateFineStatus() {
        //Given
        loanEntity.setStatus("multa pendiente");
        FineEntity anotherFine = new FineEntity();
        anotherFine.setId(2L);
        anotherFine.setLoan(loanEntity);
        anotherFine.setStatus("no pagada");

        List<FineEntity> unpaidFines = new ArrayList<>();
        unpaidFines.add(anotherFine);

        when(fineRepository.findByStatus("no pagada")).thenReturn(Optional.of(unpaidFines));
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.payFine(fineEntity);

        //Then
        assertThat(result.getStatus()).isEqualTo("pagada");
        verify(fineRepository).save(any(FineEntity.class));
        verify(loanRepository, never()).save(any(LoanEntity.class));
    }


    @Test
    void whenPayFineWithAnotherFineForSameCustomer_thenUpdateFineAndLoan() {
        //Given
        loanEntity.setStatus("multa pendiente");
        FineEntity anotherFine = new FineEntity();
        anotherFine.setId(2L);
        anotherFine.setCustomer(customerEntity);
        anotherFine.setStatus("no pagada");
        LoanEntity anotherLoan = new LoanEntity();
        anotherLoan.setId(2L);
        anotherFine.setLoan(anotherLoan);

        List<FineEntity> unpaidFines = new ArrayList<>();
        unpaidFines.add(anotherFine);

        when(fineRepository.findByStatus("no pagada")).thenReturn(Optional.of(unpaidFines));
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.payFine(fineEntity);

        //Then
        assertThat(result.getStatus()).isEqualTo("pagada");
        verify(loanRepository).save(any(LoanEntity.class));
        verify(fineRepository).save(any(FineEntity.class));
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }


    @Test
    void whenPayFineWithAnotherLoanInEvaluacionPendiente_thenUpdateFineAndLoan() {
        //Given
        loanEntity.setStatus("multa pendiente");
        LoanEntity anotherLoan = new LoanEntity();
        anotherLoan.setId(2L);
        anotherLoan.setCustomer(customerEntity);
        anotherLoan.setStatus("evaluación pendiente");

        List<FineEntity> unpaidFines = new ArrayList<>();
        List<LoanEntity> loansWithPendingEvaluation = new ArrayList<>();
        loansWithPendingEvaluation.add(anotherLoan);

        when(fineRepository.findByStatus("no pagada")).thenReturn(Optional.of(unpaidFines));
        when(loanService.findLoanByStatus("evaluación pendiente")).thenReturn((ArrayList<LoanEntity>) loansWithPendingEvaluation);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.payFine(fineEntity);

        //Then
        assertThat(result.getStatus()).isEqualTo("pagada");
        verify(loanRepository).save(any(LoanEntity.class));
        verify(fineRepository).save(any(FineEntity.class));
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }



    @Test
    void whenPayFineWithNoRestrictions_thenUpdateAllEntities() {
        //Given
        loanEntity.setStatus("multa pendiente");
        List<FineEntity> unpaidFines = new ArrayList<>();
        List<LoanEntity> loansWithPendingEvaluation = new ArrayList<>();

        when(fineRepository.findByStatus("no pagada")).thenReturn(Optional.of(unpaidFines));
        when(loanService.findLoanByStatus("evaluación pendiente")).thenReturn((ArrayList<LoanEntity>) loansWithPendingEvaluation);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);
        when(fineRepository.save(any(FineEntity.class))).thenReturn(fineEntity);

        //When
        FineEntity result = fineService.payFine(fineEntity);

        //Then
        assertThat(result.getStatus()).isEqualTo("pagada");
        verify(customerRepository).save(any(CustomerEntity.class));
        verify(loanRepository).save(any(LoanEntity.class));
        verify(fineRepository).save(any(FineEntity.class));
    }


    @Test
    void whenPayFineWithAlreadyPaidFine_thenThrowException() {
        //Given
        fineEntity.setStatus("pagada");

        //When & Then
        assertThatThrownBy(() -> fineService.payFine(fineEntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado de la multa no es correcto");
    }

    @Test
    void whenPayFineWithActiveLoan_thenThrowException() {
        //Given
        loanEntity.setStatus("activo");

        //When & Then
        assertThatThrownBy(() -> fineService.payFine(fineEntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado del prestamo no es correcto");
    }

    @Test
    void whenPayFineWithVencidoLoan_thenThrowException() {
        //Given
        loanEntity.setStatus("vencido");

        //When & Then
        assertThatThrownBy(() -> fineService.payFine(fineEntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado del prestamo no es correcto");
    }

}
