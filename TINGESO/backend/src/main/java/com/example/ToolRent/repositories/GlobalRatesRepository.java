package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.GlobalRatesEntity;
import com.example.ToolRent.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalRatesRepository extends JpaRepository<GlobalRatesEntity, Long> {
    public Optional<GlobalRatesEntity> findGlobalRatesById(Long id);

    public GlobalRatesEntity findByRateName(String rateName);
}
