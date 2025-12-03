package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    public Optional<LoanEntity> findLoanById(Long loanId);

    public Optional<List<LoanEntity>> findByReturnDate(LocalDate returnDate);

    public Optional<List<LoanEntity>> findByStatus(String status);


    // Buscar préstamos por RUT del cliente (usando query personalizada)
    @Query("SELECT l FROM LoanEntity l WHERE l.customer.rut = :rut")
    Optional<List<LoanEntity>> findByCustomerRut(@Param("rut") String rut);

    // Buscar préstamos por RUT del cliente y estado
    @Query("SELECT l FROM LoanEntity l WHERE l.customer.rut = :rut AND l.status = :status")
    Optional<List<LoanEntity>> findByCustomerRutAndStatus(@Param("rut") String rut, @Param("status") String status);



    // ========== QUERIES PARA REPORTES (ÉPICA 6) ==========

    // RF6.1: Buscar préstamos activos (vigentes y atrasados) en un rango de fechas
    @Query("SELECT l FROM LoanEntity l WHERE (l.status = 'activo' OR l.status = 'vencido' OR l.status = 'evaluación pendiente' OR l.status = 'multa pendiente') " +
            "AND l.loanDate >= :startDate AND l.loanDate <= :endDate")
    List<LoanEntity> findActiveLoansByDateRange(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // RF6.1: Buscar todos los préstamos activos (sin filtro de fecha)
    @Query("SELECT l FROM LoanEntity l WHERE l.status = 'activo' OR l.status = 'vencido' OR l.status = 'evaluación pendiente' OR l.status = 'multa pendiente'")
    List<LoanEntity> findAllActiveLoans();



    // RF6.3: Obtener ranking de herramientas más prestadas por rango de fechas
    @Query("SELECT l.tool.name, l.tool.category.name, COUNT(l) as loanCount " +
            "FROM LoanEntity l WHERE l.loanDate >= :startDate AND l.loanDate <= :endDate " +
            "GROUP BY l.tool.name, l.tool.category.name ORDER BY loanCount DESC")
    List<Object[]> findMostRentedToolsByDateRange(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    // RF6.3: Obtener ranking de herramientas más prestadas (sin filtro de fecha)
    @Query("SELECT l.tool.name, l.tool.category.name, COUNT(l) as loanCount " +
            "FROM LoanEntity l GROUP BY l.tool.name, l.tool.category.name ORDER BY loanCount DESC")
    List<Object[]> findMostRentedTools();
}
