package com.example.ToolRent.services;

import com.example.ToolRent.entities.KardexEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.repositories.KardexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KardexServiceTest {

    @Mock
    private KardexRepository kardexRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private KardexService kardexService;

    private KardexEntity kardexEntity;
    private ToolEntity toolEntity;

    @BeforeEach
    void setUp() {
        toolEntity = new ToolEntity();
        toolEntity.setId(1L);
        toolEntity.setName("Martillo");

        kardexEntity = new KardexEntity();
        kardexEntity.setId(1L);
        kardexEntity.setType("ingreso");
        kardexEntity.setDate(LocalDate.now());
        kardexEntity.setTool(toolEntity);
        kardexEntity.setUsername("testuser");
        kardexEntity.setAffectedAmount(1);
    }

    // ==================== getCurrentUsername ====================

    @Test
    void whenGetCurrentUsernameWithAuthenticatedUser_thenReturnUsername() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");

        //When
        String result = kardexService.getCurrentUsername();

        //Then
        assertThat(result).isEqualTo("testuser");
    }

    @Test
    void whenGetCurrentUsernameWithoutAuthentication_thenReturnSystem() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        //When
        String result = kardexService.getCurrentUsername();

        //Then
        assertThat(result).isEqualTo("system");
    }

    @Test
    void whenGetCurrentUsernameWithNonJwtPrincipal_thenReturnSystem() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not-a-jwt");

        //When
        String result = kardexService.getCurrentUsername();

        //Then
        assertThat(result).isEqualTo("system");
    }

    // ==================== normalizeString ====================

    @Test
    void whenNormalizeStringWithValidString_thenReturnNormalizedString() {
        //When
        String result = kardexService.normalizeString("  INGRESO  ");

        //Then
        assertThat(result).isEqualTo("ingreso");
    }

    @Test
    void whenNormalizeStringWithNull_thenReturnNull() {
        //When
        String result = kardexService.normalizeString(null);

        //Then
        assertThat(result).isNull();
    }

    // ==================== getAllMoves ====================

    @Test
    void whenGetAllMoves_thenReturnAllKardexEntities() {
        //Given
        List<KardexEntity> kardexList = new ArrayList<>();
        kardexList.add(kardexEntity);
        when(kardexRepository.findAll()).thenReturn(kardexList);

        //When
        ArrayList<KardexEntity> result = kardexService.getAllMoves();

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getType()).isEqualTo("ingreso");
    }

    // ==================== registerMovement ====================

    @Test
    void whenRegisterMovementWithValidTypeIngreso_thenSaveAndReturnKardex() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");
        when(kardexRepository.save(any(KardexEntity.class))).thenReturn(kardexEntity);

        //When
        KardexEntity result = kardexService.registerMovement("INGRESO", toolEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("ingreso");
        assertThat(result.getTool()).isEqualTo(toolEntity);
        assertThat(result.getAffectedAmount()).isEqualTo(1);
        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    @Test
    void whenRegisterMovementWithValidTypePrestamo_thenSaveAndReturnKardex() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");
        kardexEntity.setType("préstamo");
        when(kardexRepository.save(any(KardexEntity.class))).thenReturn(kardexEntity);

        //When
        KardexEntity result = kardexService.registerMovement("  PrÉsTaMo  ", toolEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("préstamo");
        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    @Test
    void whenRegisterMovementWithValidTypeDevolucion_thenSaveAndReturnKardex() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");
        kardexEntity.setType("devolución");
        when(kardexRepository.save(any(KardexEntity.class))).thenReturn(kardexEntity);

        //When
        KardexEntity result = kardexService.registerMovement("devolución", toolEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("devolución");
        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    @Test
    void whenRegisterMovementWithValidTypeBaja_thenSaveAndReturnKardex() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");
        kardexEntity.setType("baja");
        when(kardexRepository.save(any(KardexEntity.class))).thenReturn(kardexEntity);

        //When
        KardexEntity result = kardexService.registerMovement("baja", toolEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("baja");
        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    @Test
    void whenRegisterMovementWithValidTypeReparacion_thenSaveAndReturnKardex() {
        //Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");
        kardexEntity.setType("reparación");
        when(kardexRepository.save(any(KardexEntity.class))).thenReturn(kardexEntity);

        //When
        KardexEntity result = kardexService.registerMovement("reparación", toolEntity);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("reparación");
        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    @Test
    void whenRegisterMovementWithInvalidType_thenThrowException() {
        //Given
        String invalidType = "tipo_invalido";

        //When & Then
        assertThatThrownBy(() -> kardexService.registerMovement(invalidType, toolEntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El tipo de prestamo no es valido");
        verify(kardexRepository, never()).save(any(KardexEntity.class));
    }

    // ==================== getToolMovementHistory ====================

    @Test
    void whenGetToolMovementHistoryWithValidToolId_thenReturnMovements() {
        //Given
        Long toolId = 1L;
        List<KardexEntity> kardexList = new ArrayList<>();
        kardexList.add(kardexEntity);
        when(kardexRepository.findByToolOrderByDateDesc(toolId)).thenReturn(kardexList);

        //When
        List<KardexEntity> result = kardexService.getToolMovementHistory(toolId);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTool().getId()).isEqualTo(toolId);
        verify(kardexRepository, times(1)).findByToolOrderByDateDesc(toolId);
    }

    @Test
    void whenGetToolMovementHistoryWithNullToolId_thenThrowException() {
        //When & Then
        assertThatThrownBy(() -> kardexService.getToolMovementHistory(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La herramienta no puede ser null");
        verify(kardexRepository, never()).findByToolOrderByDateDesc(any());
    }

    // ==================== getMovementsByDateRange ====================

    @Test
    void whenGetMovementsByDateRangeWithValidDates_thenReturnMovements() {
        //Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        List<KardexEntity> kardexList = new ArrayList<>();
        kardexList.add(kardexEntity);
        when(kardexRepository.findByDateBetweenOrderByDateDesc(startDate, endDate)).thenReturn(kardexList);

        //When
        List<KardexEntity> result = kardexService.getMovementsByDateRange(startDate, endDate);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(kardexRepository, times(1)).findByDateBetweenOrderByDateDesc(startDate, endDate);
    }

    @Test
    void whenGetMovementsByDateRangeWithNullStartDate_thenThrowException() {
        //Given
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        //When & Then
        assertThatThrownBy(() -> kardexService.getMovementsByDateRange(null, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Las fechas no pueden ser null");
        verify(kardexRepository, never()).findByDateBetweenOrderByDateDesc(any(), any());
    }

    @Test
    void whenGetMovementsByDateRangeWithNullEndDate_thenThrowException() {
        //Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);

        //When & Then
        assertThatThrownBy(() -> kardexService.getMovementsByDateRange(startDate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Las fechas no pueden ser null");
        verify(kardexRepository, never()).findByDateBetweenOrderByDateDesc(any(), any());
    }

    @Test
    void whenGetMovementsByDateRangeWithStartDateAfterEndDate_thenThrowException() {
        //Given
        LocalDate startDate = LocalDate.of(2025, 1, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1);

        //When & Then
        assertThatThrownBy(() -> kardexService.getMovementsByDateRange(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La fecha de inicio no puede ser posterior a la fecha de fin");
        verify(kardexRepository, never()).findByDateBetweenOrderByDateDesc(any(), any());
    }

    // ==================== getToolMovementsByDateRange ====================

    @Test
    void whenGetToolMovementsByDateRangeWithValidData_thenReturnMovements() {
        //Given
        Long toolId = 1L;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        List<KardexEntity> kardexList = new ArrayList<>();
        kardexList.add(kardexEntity);
        when(kardexRepository.findByToolIdAndDateBetween(toolId, startDate, endDate)).thenReturn(kardexList);

        //When
        List<KardexEntity> result = kardexService.getToolMovementsByDateRange(toolId, startDate, endDate);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(kardexRepository, times(1)).findByToolIdAndDateBetween(toolId, startDate, endDate);
    }

    @Test
    void whenGetToolMovementsByDateRangeWithNullToolId_thenThrowException() {
        //Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        //When & Then
        assertThatThrownBy(() -> kardexService.getToolMovementsByDateRange(null, startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los parámetros no pueden ser null");
        verify(kardexRepository, never()).findByToolIdAndDateBetween(any(), any(), any());
    }

    @Test
    void whenGetToolMovementsByDateRangeWithNullStartDate_thenThrowException() {
        //Given
        Long toolId = 1L;
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        //When & Then
        assertThatThrownBy(() -> kardexService.getToolMovementsByDateRange(toolId, null, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los parámetros no pueden ser null");
        verify(kardexRepository, never()).findByToolIdAndDateBetween(any(), any(), any());
    }

    @Test
    void whenGetToolMovementsByDateRangeWithNullEndDate_thenThrowException() {
        //Given
        Long toolId = 1L;
        LocalDate startDate = LocalDate.of(2025, 1, 1);

        //When & Then
        assertThatThrownBy(() -> kardexService.getToolMovementsByDateRange(toolId, startDate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los parámetros no pueden ser null");
        verify(kardexRepository, never()).findByToolIdAndDateBetween(any(), any(), any());
    }

    @Test
    void whenGetToolMovementsByDateRangeWithStartDateAfterEndDate_thenThrowException() {
        //Given
        Long toolId = 1L;
        LocalDate startDate = LocalDate.of(2025, 1, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1);

        //When & Then
        assertThatThrownBy(() -> kardexService.getToolMovementsByDateRange(toolId, startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La fecha de inicio no puede ser posterior a la fecha de fin");
        verify(kardexRepository, never()).findByToolIdAndDateBetween(any(), any(), any());
    }
}