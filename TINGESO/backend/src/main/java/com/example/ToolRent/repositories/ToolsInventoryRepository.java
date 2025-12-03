package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.entities.ToolsInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolsInventoryRepository extends JpaRepository<ToolsInventoryEntity,Long> {
    public ToolsInventoryEntity findByName(String name);
    public ToolsInventoryEntity findByNameAndCategory(String name, String category);
    public List<ToolsInventoryEntity> findByCategory(String category);

}
