package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.entities.FineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<FineEntity,Long> {
    public Optional<List<FineEntity>> findByStatus(String status);

    public Optional<List<FineEntity>> findByType(String type);

    // Buscar multas por RUT del cliente (usando query personalizada)
    @Query("SELECT f FROM FineEntity f WHERE f.customer.rut = :rut")
    Optional<List<FineEntity>> findFineByCustomerRut(@Param("rut") String rut);

    //Buscar multas por Rut del cliente y estado
    @Query("SELECT f FROM FineEntity f WHERE f.customer.rut = :rut AND f.status = :status")
    Optional<List<FineEntity>> findFineByCustomerRutAndStatus(@Param("rut") String rut, @Param("status") String status);

    //Buscar multas por Rut del cliente y estado
    @Query("SELECT f FROM FineEntity f WHERE f.customer.rut = :rut AND f.type = :type")
    Optional<List<FineEntity>> findFineByCustomerRutAndType(@Param("rut") String rut, @Param("type") String type);

    @Query("SELECT f FROM FineEntity f WHERE f.customer.rut = :rut AND f.status = :status AND f.type = :type")
    Optional<List<FineEntity>> findFineByCustomerRutAndStatusAndType(
            @Param("rut") String rut,
            @Param("status") String status,
            @Param("type") String type
    );

    //RF6: Buscar clientes con multas de atraso no pagadas por rango de fechas del préstamo
    //Es return date porque finalmente ahi es donde comenzo el atraso
    @Query("SELECT DISTINCT f.customer FROM FineEntity f " +
            "WHERE f.status = 'no pagada' AND f.type = 'atraso' " +
            "AND f.loan.returnDate >= :startDate AND f.loan.returnDate <= :endDate")
    List<CustomerEntity> findCustomersWithOverdueLoansByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Sin filtro de fecha
    @Query("SELECT DISTINCT f.customer FROM FineEntity f " +
            "WHERE f.status = 'no pagada' AND f.type = 'atraso'")
    List<CustomerEntity> findCustomersWithOverdueLoans();

}
