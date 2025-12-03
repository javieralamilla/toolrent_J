package com.example.ToolRent.services;

import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    //Mock crea un falso repostorio para simular el comportamiento real sin acceder a la base de datos
    @Mock
    private CustomerRepository customerRepository;

    //Crea una instancia de CustomerService e inyecta el mock CustomerRepository en él
    @InjectMocks
    private CustomerService customerService;

    private CustomerEntity validCustomer;

    @BeforeEach
    void setup() {
        validCustomer = new CustomerEntity();
        validCustomer.setId(1L);
        validCustomer.setRut("12.345.678-5");
        validCustomer.setEmail("test@example.com");
        validCustomer.setPhoneNumber("912345678");
        validCustomer.setStatus("activo");
    }

    // ====================Rut Validation Tests====================
    //validateRut

    @Test
    void whenValidateRutWithValidRut_thenTrue() {
        // Given
        String validRut = "24.027.977-0";

        // When
        boolean result = CustomerService.validateRut(validRut);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void whenValidateRutWithDigitK_thenReturnTrue() {
        // Given
        String validRutK = "19.285.394-k";

        // When
        boolean result = CustomerService.validateRut(validRutK);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void whenValidateRutWithIvalidrRut_thenReturnFalse() {
        // Given
        String invalidRut = "12.345.678-9";

        // When
        boolean result = CustomerService.validateRut(invalidRut);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateRutWithShortRut_thenReturnFalse() {
        // Given
        String shortRut = "123-4";

        // When
        boolean result = CustomerService.validateRut(shortRut);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateRutWithLettersInBody_thenReturnFalse() {
        //Given
        String rutWithLetters = "12.ABC.678-5";

        //When
        boolean result = CustomerService.validateRut(rutWithLetters);

        //Then
        assertThat(result).isFalse();
    }

    // ==================== RUT FORMAT TESTS ====================
    //formatRut

    @Test
    void whenFormatRutWithUnformattedRut_thenReturnFormattedRut() {
        //Given
        String unformattedRut = "123456785";

        //When
        String result = CustomerService.formatRut(unformattedRut);

        //Then
        assertThat(result).isEqualTo("12.345.678-5");
    }

    @Test
    void whenFormatRutWithAlreadyFormattedRut_thenReturnSameFormat() {
        //Given
        String formattedRut = "12.345.678-5";

        //When
        String result = CustomerService.formatRut(formattedRut);

        //Then
        assertThat(result).isEqualTo("12.345.678-5");
    }

    @Test
    void whenFormatRutWithLowercaseK_thenReturnUppercaseK() {
        //Given
        String rutWithLowercaseK = "11111111-k";

        //When
        String result = CustomerService.formatRut(rutWithLowercaseK);

        //Then
        assertThat(result).isEqualTo("11.111.111-K");
    }

    @Test
    void whenValidateRutWithShortRut_thenReturnShortRut() {
        // Given
        String shortRut = "1-4";

        // When
        String result = CustomerService.formatRut(shortRut);

        // Then
        assertThat(result).isEqualTo("14");
    }


    // ==================== EMAIL VALIDATION TESTS ====================
    //isValidEmail

    @Test
    void whenValidateEmailWithValidEmail_thenReturnTrue() {
        //Given
        String validEmail = "test@example.com";

        //When
        boolean result = CustomerService.isValidEmail(validEmail);

        //Then
        assertThat(result).isTrue();
    }

    @Test
    void whenValidateEmailWithoutAtSymbol_thenReturnFalse() {
        //Given
        String emailWithoutAt = "testexample.com";

        //When
        boolean result = CustomerService.isValidEmail(emailWithoutAt);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateEmailWithoutDomain_thenReturnFalse() {
        //Given
        String emailWithoutDomain = "test@";

        //When
        boolean result = CustomerService.isValidEmail(emailWithoutDomain);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateEmailWithNullEmail_thenReturnFalse() {
        //Given
        String nullEmail = null;

        //When
        boolean result = CustomerService.isValidEmail(nullEmail);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateEmailWithEmptyEmail_thenReturnFalse() {
        //Given
        String emptyEmail = "   ";

        //When
        boolean result = CustomerService.isValidEmail(emptyEmail);

        //Then
        assertThat(result).isFalse();
    }

    // ==================== PHONE VALIDATION TESTS ====================
    //validateCellPhone

    @Test
    void whenValidateCellPhoneWithValidFormat_thenReturnFalse() {
        //Given - Note: metodo retorna false cuando formato ES válido
        String validPhone = "912345678";

        //When
        boolean result = CustomerService.validateCellPhone(validPhone);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateCellPhoneWithPrefix56_thenReturnFalse() {
        //Given
        String phoneWithPrefix = "56912345678";

        //When
        boolean result = CustomerService.validateCellPhone(phoneWithPrefix);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateCellPhoneWithPrefixPlus56_thenReturnFalse() {
        //Given
        String phoneWithPrefix = "+56912345678";

        //When
        boolean result = CustomerService.validateCellPhone(phoneWithPrefix);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateCellPhoneWithouthPrefix_thenReturnFalse() {
        //Given
        String phoneWithouthPrefix = "12345678";

        //When
        boolean result = CustomerService.validateCellPhone(phoneWithouthPrefix);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenValidateCellPhoneWithInvalidFormat_thenReturnTrue() {
        //Given
        String invalidPhone = "12345";

        //When
        boolean result = CustomerService.validateCellPhone(invalidPhone);

        //Then
        assertThat(result).isTrue();
    }

    @Test
    void whenValidateCellPhoneWithNullPhone_thenReturnTrue() {
        //Given
        String nullPhone = null;

        //When
        boolean result = CustomerService.validateCellPhone(nullPhone);

        //Then
        assertThat(result).isTrue();
    }

    // ==================== PHONE FORMAT TESTS ====================
    //formatCellPhone

    @Test
    void whenFormatCellPhoneWithPlusAndElevenDigits_thenAddNineAndFormat() {
        //Given
        String phone = "+56912345678";

        //When
        String result = CustomerService.formatCellPhone(phone);

        //Then
        assertThat(result).isEqualTo("+56 9 1234 5678");
    }

    @Test
    void whenFormatCellPhoneWithElevenDigits_thenAddNineAndFormat() {
        //Given
        String phone = "56912345678";

        //When
        String result = CustomerService.formatCellPhone(phone);

        //Then
        assertThat(result).isEqualTo("+56 9 1234 5678");
    }

    @Test
    void whenFormatCellPhoneWithNineDigits_thenReturnFormattedPhone() {
        //Given
        String phone = "912345678";

        //When
        String result = CustomerService.formatCellPhone(phone);

        //Then
        assertThat(result).isEqualTo("+56 9 1234 5678");
    }

    @Test
    void whenFormatCellPhoneWithEightDigits_thenAddNineAndFormat() {
        //Given
        String phone = "12345678";

        //When
        String result = CustomerService.formatCellPhone(phone);

        //Then
        assertThat(result).isEqualTo("+56 9 1234 5678");
    }


    @Test
    void whenFormatCellPhoneWithSpacesAndDashes_thenCleanAndFormat() {
        //Given
        String phoneWithSpaces = "9-1234-5678";

        //When
        String result = CustomerService.formatCellPhone(phoneWithSpaces);

        //Then
        assertThat(result).isEqualTo("+56 9 1234 5678");
    }

    // ==================== VALIDATE STATUS TRANSITION TESTS ====================


    @Test
    void whenValidateStatusTransitionWithActivoStatus_thenReturnNormalizedStatus() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("activo");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("restringido");

        //When
        String result = CustomerService.validateStatusTransition(updatedCustomer, existingCustomer);

        //Then
        assertThat(result).isEqualTo("activo");
    }

    @Test
    void whenValidateStatusTransitionWithRestringidoStatus_thenReturnNormalizedStatus() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("restringido");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("activo");

        //When
        String result = CustomerService.validateStatusTransition(updatedCustomer, existingCustomer);

        //Then
        assertThat(result).isEqualTo("restringido");
    }

    @Test
    void whenValidateStatusTransitionWithUppercaseStatus_thenReturnLowercaseNormalized() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("ACTIVO");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("restringido");

        //When
        String result = CustomerService.validateStatusTransition(updatedCustomer, existingCustomer);

        //Then
        assertThat(result).isEqualTo("activo");
    }

    @Test
    void whenValidateStatusTransitionWithMixedCaseStatus_thenReturnLowercaseNormalized() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("ReStRiNgIdO");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("activo");

        //When
        String result = CustomerService.validateStatusTransition(updatedCustomer, existingCustomer);

        //Then
        assertThat(result).isEqualTo("restringido");
    }

    @Test
    void whenValidateStatusTransitionWithWhitespaceInStatus_thenReturnTrimmedStatus() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("  activo  ");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("restringido");

        //When
        String result = CustomerService.validateStatusTransition(updatedCustomer, existingCustomer);

        //Then
        assertThat(result).isEqualTo("activo");
    }

    @Test
    void whenValidateStatusTransitionWithInvalidStatus_thenThrowException() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("eliminado");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("activo");

        //When & Then
        //Este codigo verifica que se lance una excepción IllegalArgumentException con un mensaje específico
        assertThatThrownBy(() -> CustomerService.validateStatusTransition(updatedCustomer, existingCustomer))

                //La excepción debe ser IllegalArgumentException
                .isInstanceOf(IllegalArgumentException.class)
                //El mensaje de la excepcion debe contener este texto
                .hasMessageContaining("Estado inválido")
                .hasMessageContaining("Solo se permite 'activo' o 'restringido'");
    }

    @Test
    void whenValidateStatusTransitionWithEmptyStatus_thenThrowException() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("activo");

        //When & Then
        assertThatThrownBy(() -> CustomerService.validateStatusTransition(updatedCustomer, existingCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado inválido");
    }

    @Test
    void whenValidateStatusTransitionWithRandomString_thenThrowException() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("pendiente");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("activo");

        //When & Then
        assertThatThrownBy(() -> CustomerService.validateStatusTransition(updatedCustomer, existingCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado inválido");
    }

    @Test
    void whenValidateStatusTransitionFromActivoToRestringido_thenReturnRestringido() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("restringido");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("activo");

        //When
        String result = CustomerService.validateStatusTransition(updatedCustomer, existingCustomer);

        //Then
        assertThat(result).isEqualTo("restringido");
    }

    @Test
    void whenValidateStatusTransitionFromRestringidoToActivo_thenReturnActivo() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setStatus("activo");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setStatus("restringido");

        //When
        String result = CustomerService.validateStatusTransition(updatedCustomer, existingCustomer);

        //Then
        assertThat(result).isEqualTo("activo");
    }

    // ==================== UNIQUENESS CHECK TESTS ====================

    //1) isRutAlreadyRegistered

    @Test
    void whenIsRutAlreadyRegisteredWithExistingRut_thenReturnTrue() {
        //Given
        when(customerRepository.findByRut("12.345.678-5"))
                .thenReturn(Optional.of(validCustomer));

        //When
        boolean result = customerService.isRutAlreadyRegistered("12.345.678-5");

        //Then
        assertThat(result).isTrue();
    }

    @Test
    void whenIsRutAlreadyRegisteredWithNonExistingRut_thenReturnFalse() {
        //Given
        when(customerRepository.findByRut("99.999.999-9"))
                .thenReturn(Optional.empty());

        //When
        boolean result = customerService.isRutAlreadyRegistered("99.999.999-9");

        //Then
        assertThat(result).isFalse();
    }

    // 2) isEmailAlreadyRegistered

    @Test
    void whenIsEmailAlreadyRegisteredWithNonExistingEmail_thenReturnFalse() {
        //Given
        when(customerRepository.findByEmail("juan@example.com"))
                .thenReturn(null);

        //When
        boolean result = customerService.isEmailAlreadyRegistered("juan@example.com");

        //Then
        assertThat(result).isFalse();
    }


    @Test
    void whenIsEmailAlreadyRegisteredWithExistingEmail_thenReturnTrue() {
        //Given
        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(validCustomer);

        //When
        boolean result = customerService.isEmailAlreadyRegistered("test@example.com");

        //Then
        assertThat(result).isTrue();
    }


    // 3) isPhoneNumberAlreadyRegistered

    @Test
    void whenIsPhoneAlreadyRegisteredWithNonExistingPhone_thenReturnFalse() {
        //Given
        when(customerRepository.findByPhoneNumber("+56 9 1111 1111"))
                .thenReturn(null);

        //When
        boolean result = customerService.isPhoneAlreadyRegistered("+56 9 1111 1111");

        //Then
        assertThat(result).isFalse();
    }

    @Test
    void whenIsPhoneAlreadyRegisteredWithExistingPhone_thenReturnTrue() {
        //Given
        when(customerRepository.findByPhoneNumber("+56 9 1234 5678"))
                .thenReturn(validCustomer);

        //When
        boolean result = customerService.isPhoneAlreadyRegistered("+56 9 1234 5678");

        //Then
        assertThat(result).isTrue();
    }

    // ==================== GET METHODS TESTS ====================

    //==================== getCustomers ====================

    @Test
    void whenGetCustomers_thenReturnCustomerList() {
        //Given
        ArrayList<CustomerEntity> customers = new ArrayList<>(Arrays.asList(
                validCustomer,
                new CustomerEntity()
        ));
        when(customerRepository.findAll()).thenReturn(customers);

        //When
        ArrayList<CustomerEntity> result = customerService.getCustomers();

        //Then
        assertThat(result).hasSize(2);
        verify(customerRepository, times(1)).findAll();
    }

    //==================== getCustomerByRut ====================

    @Test
    void whenGetCustomerByRutWithExistingRut_thenReturnCustomer() {
        //Given
        when(customerRepository.findByRut("12.345.678-5"))
                .thenReturn(Optional.of(validCustomer));

        //When
        CustomerEntity result = customerService.getCustomerByRut("12.345.678-5");

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo("12.345.678-5");
        verify(customerRepository).findByRut("12.345.678-5");
    }

    @Test
    void whenGetCustomerByRutWithNonExistingRut_thenThrowException() {
        //Given
        when(customerRepository.findByRut("99.999.999-9"))
                .thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> customerService.getCustomerByRut("99.999.999-9"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrado");
    }

    //==================== getCustomerById ====================

    @Test
    void whenGetCustomerByIdWithExistingId_thenReturnCustomer() {
        //Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(validCustomer));

        //When
        CustomerEntity result = customerService.getCustomerById(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void whenGetCustomerByIdWithNonExistingId_thenReturnCustomer() {
        //Given
        when(customerRepository.findById(3L))
                .thenReturn(Optional.empty());


        //When & Then
        assertThatThrownBy(() -> customerService.getCustomerById(3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrada");
    }

    //==================== getCustomerByStatus ====================

    @Test
    void whenGetCustomerByStatus_thenReturnCustomersWithStatus() {
        //Given
        ArrayList<CustomerEntity> activeCustomers = new ArrayList<>(Collections.singletonList(validCustomer));
        when(customerRepository.findByStatus("activo")).thenReturn(activeCustomers);

        //When
        ArrayList<CustomerEntity> result = customerService.getCustomerByStatus("activo");

        //Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("activo");
    }

    //==================== SAVE CUSTOMER TESTS ====================

    @Test
    void whenSaveCustomerWithValidData_thenSaveSuccessfully() {
        //Given
        CustomerEntity newCustomer = new CustomerEntity();
        newCustomer.setRut("10296678-3");
        newCustomer.setName("Pedro");
        newCustomer.setEmail("pedro@test.com");
        newCustomer.setPhoneNumber("912345679");

        when(customerRepository.save(any())).thenReturn(newCustomer);

        //When
        CustomerEntity result = customerService.saveCustomer(newCustomer);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("activo");
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    void whenSaveCustomerWithInvalidRut_thenThrowException() {
        //Given
        CustomerEntity invalidCustomer = new CustomerEntity();
        invalidCustomer.setRut("12345678-0");

        //When & Then
        assertThatThrownBy(() -> customerService.saveCustomer(invalidCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rut ingresado es invalido");
    }

    @Test
    void whenSaveCustomerWithDuplicateRut_thenThrowException() {
        //Given
        CustomerEntity newCustomer = new CustomerEntity();
        newCustomer.setRut("12345678-5");
        newCustomer.setName("Juan");
        newCustomer.setEmail("nuevo@test.com");
        newCustomer.setPhoneNumber("987654321");

        when(customerRepository.findByRut(any())).thenReturn(Optional.of(validCustomer));

        //When & Then
        assertThatThrownBy(() -> customerService.saveCustomer(newCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RUT ya está registrado en el sistema");
    }

    @Test
    void whenSaveCustomerWithInvalidEmail_thenThrowException() {
        //Given
        CustomerEntity invalidCustomer = new CustomerEntity();
        invalidCustomer.setRut("12345678-5");
        invalidCustomer.setEmail("emailinvalido");


        //When & Then
        assertThatThrownBy(() -> customerService.saveCustomer(invalidCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email no es válido");
    }

    @Test
    void whenSaveCustomerWithDuplicateEmail_thenThrowException() {
        //Given
        CustomerEntity newCustomer = new CustomerEntity();
        newCustomer.setRut("10.296.678-3");
        newCustomer.setName("Maria");
        newCustomer.setEmail("test@example.com");
        newCustomer.setPhoneNumber("987654321");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setEmail("test@example.com");

        when(customerRepository.findByRut("10.296.678-3")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(existingCustomer);

        //When & Then
        assertThatThrownBy(() -> customerService.saveCustomer(newCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El email ya está registrado en el sistema");
    }

    @Test
    void whenSaveCustomerWithInvalidPhone_thenThrowException() {
        //Given
        CustomerEntity invalidCustomer = new CustomerEntity();
        invalidCustomer.setRut("10.296.678-3");
        invalidCustomer.setName("Carlos");
        invalidCustomer.setEmail("carlos@test.com");
        invalidCustomer.setPhoneNumber("12345"); // Teléfono inválido

        when(customerRepository.findByRut("10.296.678-3")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("carlos@test.com")).thenReturn(null);

        //When & Then
        assertThatThrownBy(() -> customerService.saveCustomer(invalidCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("formato del celular no es válido");
    }

    @Test
    void whenSaveCustomerWithDuplicatePhone_thenThrowException() {
        //Given
        CustomerEntity newCustomer = new CustomerEntity();
        newCustomer.setRut("10.296.678-3");
        newCustomer.setName("Ana");
        newCustomer.setEmail("ana@test.com");
        newCustomer.setPhoneNumber("912345678");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setPhoneNumber("+56 9 1234 5678");

        when(customerRepository.findByRut("10.296.678-3")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("ana@test.com")).thenReturn(null);
        when(customerRepository.findByPhoneNumber("+56 9 1234 5678"))
                .thenReturn(existingCustomer);

        //When & Then
        assertThatThrownBy(() -> customerService.saveCustomer(newCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("telefono celular ya está registrado en el sistema");
    }

    //==================== UPDATE CUSTOMER TESTS ====================

    @Test
    void whenUpdateCustomerWithValidData_thenUpdateSuccessfully() {
        //Given
        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setId(1L);
        updatedCustomer.setRut("12.345.678-5");
        updatedCustomer.setStatus("restringido");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(validCustomer));
        when(customerRepository.save(any())).thenReturn(updatedCustomer);

        //When
        CustomerEntity result = customerService.updateCustomer(updatedCustomer);

        //Then
        assertThat(result).isNotNull();
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    void whenUpdateCustomerWithNonExistingId_thenThrowException() {
        //Given
        CustomerEntity customer = new CustomerEntity();
        customer.setId(999L);

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> customerService.updateCustomer(customer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void whenUpdateCustomerChangingRut_thenThrowException() {
        //Given
        CustomerEntity customerWithChangedRut = new CustomerEntity();
        customerWithChangedRut.setId(1L);
        customerWithChangedRut.setRut("98.765.432-1");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(validCustomer));

        //When & Then
        assertThatThrownBy(() -> customerService.updateCustomer(customerWithChangedRut))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede modificar el RUT");
    }

    @Test
    void whenUpdateCustomerWithInvalidStatus_thenThrowException() {
        //Given
        CustomerEntity customerWithInvalidStatus = new CustomerEntity();
        customerWithInvalidStatus.setId(1L);
        customerWithInvalidStatus.setRut("12.345.678-5");
        customerWithInvalidStatus.setStatus("eliminado");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(validCustomer));

        //When & Then
        assertThatThrownBy(() -> customerService.updateCustomer(customerWithInvalidStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado inválido");
    }

    // ==================== HELPER METHODS TESTS ====================

    // 1) isActive
    @Test
    void whenIsActiveWithActiveStatus_thenReturnTrue() {
        //Given
        String status = "activo";

        //When
        boolean result = customerService.isActive(status);

        //Then
        assertThat(result).isTrue();
    }

    @Test
    void whenIsActiveWithNonActiveStatus_thenReturnFalse() {
        //Given
        String status = "restringido";

        //When
        boolean result = customerService.isActive(status);

        //Then
        assertThat(result).isFalse();
    }
}
