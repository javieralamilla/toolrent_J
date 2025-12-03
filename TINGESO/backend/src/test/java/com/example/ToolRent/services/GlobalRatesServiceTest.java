package com.example.ToolRent.services;

import com.example.ToolRent.entities.GlobalRatesEntity;
import com.example.ToolRent.repositories.GlobalRatesRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalRatesServiceTest {

    @Mock
    private GlobalRatesRepository globalRatesRepository;

    @InjectMocks
    private GlobalRatesService globalRatesService;

    private GlobalRatesEntity globalRatesEntity;

    @BeforeEach
    void setUp() {
        globalRatesEntity = new GlobalRatesEntity();
        globalRatesEntity.setId(1L);
        globalRatesEntity.setRateName("Tarifa Estándar");
        globalRatesEntity.setDailyRateValue(5000);
    }

    // ==================== findGlobalRatesById ====================

    @Test
    void whenFindGlobalRatesByIdWithValidId_thenReturnGlobalRates() {
        //Given
        Long id = 1L;
        when(globalRatesRepository.findGlobalRatesById(id))
                .thenReturn(Optional.of(globalRatesEntity));

        //When
        GlobalRatesEntity result = globalRatesService.findGlobalRatesById(id);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getRateName()).isEqualTo("Tarifa Estándar");
        assertThat(result.getDailyRateValue()).isEqualTo(5000);
        verify(globalRatesRepository, times(1)).findGlobalRatesById(id);
    }

    @Test
    void whenFindGlobalRatesByIdWithInvalidId_thenThrowException() {
        //Given
        Long id = 999L;
        when(globalRatesRepository.findGlobalRatesById(id))
                .thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> globalRatesService.findGlobalRatesById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tarifa global con el id " + id + " no encontrada");
        verify(globalRatesRepository, times(1)).findGlobalRatesById(id);
    }

    // ==================== findByRateName ====================

    @Test
    void whenFindByRateNameWithValidName_thenReturnGlobalRates() {
        //Given
        String rateName = "Tarifa Estándar";
        when(globalRatesRepository.findByRateName(rateName))
                .thenReturn(globalRatesEntity);

        //When
        GlobalRatesEntity result = globalRatesService.findByRateName(rateName);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getRateName()).isEqualTo(rateName);
        assertThat(result.getDailyRateValue()).isEqualTo(5000);
        verify(globalRatesRepository, times(1)).findByRateName(rateName);
    }

    @Test
    void whenFindByRateNameWithInvalidName_thenReturnNull() {
        //Given
        String rateName = "Tarifa Inexistente";
        when(globalRatesRepository.findByRateName(rateName))
                .thenReturn(null);

        //When
        GlobalRatesEntity result = globalRatesService.findByRateName(rateName);

        //Then
        assertThat(result).isNull();
        verify(globalRatesRepository, times(1)).findByRateName(rateName);
    }

    // ==================== getGlobalRates ====================

    @Test
    void whenGetGlobalRates_thenReturnAllRates() {
        //Given
        GlobalRatesEntity globalRatesEntity2 = new GlobalRatesEntity();
        globalRatesEntity2.setId(2L);
        globalRatesEntity2.setRateName("Tarifa Premium");
        globalRatesEntity2.setDailyRateValue(10000);

        List<GlobalRatesEntity> ratesList = new ArrayList<>();
        ratesList.add(globalRatesEntity);
        ratesList.add(globalRatesEntity2);

        when(globalRatesRepository.findAll()).thenReturn(ratesList);

        //When
        ArrayList<GlobalRatesEntity> result = globalRatesService.getGlobalRates();

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getRateName()).isEqualTo("Tarifa Estándar");
        assertThat(result.get(1).getRateName()).isEqualTo("Tarifa Premium");
        verify(globalRatesRepository, times(1)).findAll();
    }

    @Test
    void whenGetGlobalRatesWithEmptyList_thenReturnEmptyList() {
        //Given
        when(globalRatesRepository.findAll()).thenReturn(new ArrayList<>());

        //When
        ArrayList<GlobalRatesEntity> result = globalRatesService.getGlobalRates();

        //Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(globalRatesRepository, times(1)).findAll();
    }

    // ==================== saveRate ====================

    @Test
    void whenSaveRateWithValidData_thenReturnSavedRate() {
        //Given
        when(globalRatesRepository.save(any(GlobalRatesEntity.class)))
                .thenReturn(globalRatesEntity);

        //When
        GlobalRatesEntity result = globalRatesService.saveRate(globalRatesEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getRateName()).isEqualTo("Tarifa Estándar");
        assertThat(result.getDailyRateValue()).isEqualTo(5000);
        verify(globalRatesRepository, times(1)).save(globalRatesEntity);
    }

    @Test
    void whenSaveRateWithMinimumValidValue_thenReturnSavedRate() {
        //Given
        globalRatesEntity.setDailyRateValue(1500);
        when(globalRatesRepository.save(any(GlobalRatesEntity.class)))
                .thenReturn(globalRatesEntity);

        //When
        GlobalRatesEntity result = globalRatesService.saveRate(globalRatesEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getDailyRateValue()).isEqualTo(1500);
        verify(globalRatesRepository, times(1)).save(globalRatesEntity);
    }

    @Test
    void whenSaveRateWithMaximumValidValue_thenReturnSavedRate() {
        //Given
        globalRatesEntity.setDailyRateValue(25000);
        when(globalRatesRepository.save(any(GlobalRatesEntity.class)))
                .thenReturn(globalRatesEntity);

        //When
        GlobalRatesEntity result = globalRatesService.saveRate(globalRatesEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getDailyRateValue()).isEqualTo(25000);
        verify(globalRatesRepository, times(1)).save(globalRatesEntity);
    }


    @Test
    void whenSaveRateWithNegativeValue_thenThrowException() {
        //Given
        globalRatesEntity.setDailyRateValue(-100);

        //When & Then
        assertThatThrownBy(() -> globalRatesService.saveRate(globalRatesEntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta no puede ser negativo");
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }

    @Test
    void whenSaveRateWithValueBelowMinimum_thenThrowException() {
        //Given
        globalRatesEntity.setDailyRateValue(1000);

        //When & Then
        assertThatThrownBy(() -> globalRatesService.saveRate(globalRatesEntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta debe ser mínimo $1.500 CLP");
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }

    @Test
    void whenSaveRateWithValueAboveMaximum_thenThrowException() {
        //Given
        globalRatesEntity.setDailyRateValue(30000);

        //When & Then
        assertThatThrownBy(() -> globalRatesService.saveRate(globalRatesEntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta no puede exceder $25.000 CLP");
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }

    // ==================== updateValueRate ====================

    @Test
    void whenUpdateValueRateWithValidData_thenReturnUpdatedRate() {
        //Given
        Long id = 1L;
        Integer newValue = 7000;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.of(globalRatesEntity));
        when(globalRatesRepository.save(any(GlobalRatesEntity.class)))
                .thenReturn(globalRatesEntity);

        //When
        GlobalRatesEntity result = globalRatesService.updateValueRate(id, newValue);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getDailyRateValue()).isEqualTo(newValue);
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, times(1)).save(globalRatesEntity);
    }

    @Test
    void whenUpdateValueRateWithMinimumValidValue_thenReturnUpdatedRate() {
        //Given
        Long id = 1L;
        Integer newValue = 1500;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.of(globalRatesEntity));
        when(globalRatesRepository.save(any(GlobalRatesEntity.class)))
                .thenReturn(globalRatesEntity);

        //When
        GlobalRatesEntity result = globalRatesService.updateValueRate(id, newValue);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getDailyRateValue()).isEqualTo(newValue);
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, times(1)).save(globalRatesEntity);
    }

    @Test
    void whenUpdateValueRateWithMaximumValidValue_thenReturnUpdatedRate() {
        //Given
        Long id = 1L;
        Integer newValue = 25000;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.of(globalRatesEntity));
        when(globalRatesRepository.save(any(GlobalRatesEntity.class)))
                .thenReturn(globalRatesEntity);

        //When
        GlobalRatesEntity result = globalRatesService.updateValueRate(id, newValue);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getDailyRateValue()).isEqualTo(newValue);
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, times(1)).save(globalRatesEntity);
    }

    @Test
    void whenUpdateValueRateWithInvalidId_thenThrowException() {
        //Given
        Long id = 999L;
        Integer newValue = 5000;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> globalRatesService.updateValueRate(id, newValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tarifa con ID " + id + " no encontrada");
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }

    @Test
    void whenUpdateValueRateWithNullValue_thenThrowException() {
        //Given
        Long id = 1L;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.of(globalRatesEntity));

        //When & Then
        assertThatThrownBy(() -> globalRatesService.updateValueRate(id, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta no puede ser nulo");
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }

    @Test
    void whenUpdateValueRateWithNegativeValue_thenThrowException() {
        //Given
        Long id = 1L;
        Integer newValue = -500;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.of(globalRatesEntity));

        //When & Then
        assertThatThrownBy(() -> globalRatesService.updateValueRate(id, newValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta no puede ser negativo");
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }

    @Test
    void whenUpdateValueRateWithValueBelowMinimum_thenThrowException() {
        //Given
        Long id = 1L;
        Integer newValue = 1000;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.of(globalRatesEntity));

        //When & Then
        assertThatThrownBy(() -> globalRatesService.updateValueRate(id, newValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta debe ser mínimo $1.500 CLP");
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }

    @Test
    void whenUpdateValueRateWithValueAboveMaximum_thenThrowException() {
        //Given
        Long id = 1L;
        Integer newValue = 30000;
        when(globalRatesRepository.findById(id))
                .thenReturn(Optional.of(globalRatesEntity));

        //When & Then
        assertThatThrownBy(() -> globalRatesService.updateValueRate(id, newValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de renta no puede exceder $25.000 CLP");
        verify(globalRatesRepository, times(1)).findById(id);
        verify(globalRatesRepository, never()).save(any(GlobalRatesEntity.class));
    }
}