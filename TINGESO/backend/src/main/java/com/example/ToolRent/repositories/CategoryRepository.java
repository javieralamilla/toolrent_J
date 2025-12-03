package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ToolRent.entities.CategoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    public Optional<CategoryEntity> findCategoryByName(String name);
}
