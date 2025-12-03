package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.KardexEntity;
//Proporciona CRUD basico automaticamente
import com.example.ToolRent.entities.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
//Permite definir consultas personalizadas, con SQL nativo

/*:nombre y :depto son placeholders con nombre
@Param("nombre") vincula el parámetro name con el placeholder :nombre
Ventaja: El orden de los parámetros ya no importa/*

 */
//Para detectar que es repositorio, igual lo detecta solo con JPArepo.. pero este da mas beneficios
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {
    /**
     * RF5.2 - Consultar historial de movimientos de cada herramienta
     * Busca todos los movimientos de una herramienta específica, ordenados por fecha (más reciente primero)
     */
    @Query("SELECT k FROM KardexEntity k WHERE k.tool.id = :toolId ORDER BY k.date DESC")
    List<KardexEntity> findByToolOrderByDateDesc(@Param("toolId") Long toolId);


    /**
     * RF5.3 - Generar listado de movimientos por rango de fechas
     * Busca movimientos entre dos fechas (inclusivo), ordenados por fecha
     */
    @Query("SELECT k FROM KardexEntity k WHERE k.date BETWEEN :startDate AND :endDate ORDER BY k.date DESC")
    List<KardexEntity> findByDateBetweenOrderByDateDesc(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);


    /**
     * movimientos de una herramienta en un rango de fechas
     */
    @Query("SELECT k FROM KardexEntity k WHERE k.tool.id = :toolId AND k.date BETWEEN :startDate AND :endDate ORDER BY k.date DESC")
    List<KardexEntity> findByToolIdAndDateBetween(@Param("toolId") Long toolId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
}
