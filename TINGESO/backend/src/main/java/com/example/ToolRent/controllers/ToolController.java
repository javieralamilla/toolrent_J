package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.entities.ToolsInventoryEntity;
import com.example.ToolRent.services.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Convierte automaticamente los objetos Java a JSON/XML
@RestController
//Define la ruta base para todos los endpoints del controlador
@RequestMapping("/api/v1/tools")
//permite acceso desde cualquier dominio (cualquier sitio web)
@CrossOrigin("*")

public class ToolController {
    @Autowired
    ToolService toolService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/id/{id}")
    public ResponseEntity<ToolEntity> findByToolId(@PathVariable Long id) {
        ToolEntity tool = toolService.findToolById(id);
        return ResponseEntity.ok(tool);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/name/{name}")
    public ResponseEntity<List<ToolEntity>> findByName(@PathVariable String name) {
        List<ToolEntity> tools = toolService.findByName(name);
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/name/category/{name}/{category}")
    public ResponseEntity<ToolEntity> findToolByNameAndCategory(@PathVariable String name, @PathVariable String category) {
        ToolEntity tool = toolService.findToolByNameAndCategory(name, category);
        return ResponseEntity.ok(tool);
    }


    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<ToolEntity>> listAllTools() {
        List<ToolEntity> tools = toolService.getTools();
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getToolsByStatus(@PathVariable String status) {
        try {
            List<ToolEntity> tools = toolService.getToolsByStatus(status);
            return ResponseEntity.ok(tools);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ToolEntity>> getToolsByCategory(@PathVariable String category) {
        List<ToolEntity> tools = toolService.getToolsByCategory(category);
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/inventory")
    public ResponseEntity<List<ToolsInventoryEntity>> getToolsInventory() {
        List<ToolsInventoryEntity> toolsInventory = toolService.getToolsInventory();
        return ResponseEntity.ok(toolsInventory);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/inventory/category/{category}")
    public ResponseEntity<List<ToolsInventoryEntity>> findByCategory(@PathVariable String category) {
        List<ToolsInventoryEntity> toolsInventory = toolService.findByCategory(category);
        return ResponseEntity.ok(toolsInventory);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/inventory/name/{name}")
    public ResponseEntity<ToolsInventoryEntity> findToolByName(@PathVariable String name) {
        ToolsInventoryEntity toolsInventory = toolService.findToolByName(name);
        return ResponseEntity.ok(toolsInventory);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/inventory/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            ToolsInventoryEntity toolsInventory = toolService.findById(id);
            return ResponseEntity.ok(toolsInventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/inventory/name/category/{name}/{category}")
    public ResponseEntity<?> getToolInventory(@PathVariable String name, @PathVariable String category) {
        try {
            ToolsInventoryEntity toolsInventory = toolService.getToolInventory(name, category);
            return ResponseEntity.ok(toolsInventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/inRepairWithMinorDamage")
    public ResponseEntity<List<ToolEntity>> getToolsInRepairWithMinorDamage() {
        List<ToolEntity> tools = toolService.getToolsInRepairWithMinorDamageFine();
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/replacementValue/{id}/{value}")
    public ResponseEntity<?> updateReplacementValue(@PathVariable Long id, @PathVariable Integer value) {
        try {
            ToolsInventoryEntity toolsInventory = toolService.updateReplacementValue(id, value);
            return ResponseEntity.ok(toolsInventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/dailyRentalRate/{id}/{dailyRentalRate}")
    public ResponseEntity<?> updateDailyRentalRate(@PathVariable Long id, @PathVariable Integer dailyRentalRate) {
        try {
            ToolsInventoryEntity toolsInventory = toolService.updateDailyRentalRate(id, dailyRentalRate);
            return ResponseEntity.ok(toolsInventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/{quantity}/{replacementValue}/{dailyRentalRate}")
    public ResponseEntity<?> saveTool(@RequestBody ToolEntity tool, @PathVariable int quantity, @PathVariable int replacementValue, @PathVariable int dailyRentalRate) {
        try {
            List<ToolEntity> tools = toolService.saveTool(tool, quantity, replacementValue, dailyRentalRate);
            return ResponseEntity.ok(tools);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/existing/{quantity}")
    public ResponseEntity<?> saveRegisteredTool(@RequestBody ToolEntity tool, @PathVariable int quantity) {
        try {
            List<ToolEntity> tools = toolService.saveRegisteredTool(tool, quantity);
            return ResponseEntity.ok(tools);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/repairedTool/{toolId}")
    public ResponseEntity<?> repairedTool(@PathVariable Long toolId) {
        try {
            ToolEntity tool = toolService.repairedTool(toolId);
            return ResponseEntity.ok(tool);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
