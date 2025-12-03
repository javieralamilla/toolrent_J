package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity,Long> {
    public List<ToolEntity> findByName(String name);
    public List<ToolEntity> findByStatus(String status);

    @Query("SELECT t FROM ToolEntity t WHERE t.category.name = :categoryName")
    List<ToolEntity> findByCategory(@Param("categoryName") String categoryName);

    public ToolEntity findToolById(Long id);


    @Query("SELECT t FROM ToolEntity t WHERE t.name = :toolName AND t.category.name = :categoryName")
    Optional<ToolEntity> findToolByNameAndCategory(
            @Param("toolName") String toolName,
            @Param("categoryName") String categoryName
    );

    @Query("SELECT DISTINCT t FROM ToolEntity t " +
            "JOIN LoanEntity l ON l.tool.id = t.id " +
            "JOIN FineEntity f ON f.loan.id = l.id " +
            "WHERE t.status = 'en reparacion' AND f.type = 'daño leve'")
    List<ToolEntity> findToolsInRepairWithMinorDamageFine();
}
