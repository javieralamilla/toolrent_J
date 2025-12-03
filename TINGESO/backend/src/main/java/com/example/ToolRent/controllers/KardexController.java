package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.KardexEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.services.KardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

//Convierte automaticamente los objetos Java a JSON/XML
@RestController
//Define la ruta base para todos los endpoints del controlador
@RequestMapping("/api/v1/movements")
//permite acceso desde cualquier dominio (cualquier sitio web)
@CrossOrigin("*")
public class KardexController {
    @Autowired
    KardexService kardexService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<KardexEntity>> listMoves() {
        List<KardexEntity> movements = kardexService.getAllMoves();
        return ResponseEntity.ok(movements);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/toolId/{toolId}")
    public ResponseEntity<?> getToolMovementHistory(@PathVariable Long toolId) {
        try {
            List<KardexEntity> movements = kardexService.getToolMovementHistory(toolId);
            return ResponseEntity.ok(movements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/dateRange/{startDate}/{endDate}")
    public ResponseEntity<?> getMovementsByDateRange(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        try {
            List<KardexEntity> movements = kardexService.getMovementsByDateRange(startDate, endDate);
            return ResponseEntity.ok(movements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/tool/dateRange/{id}/{startDate}/{endDate}")
    public ResponseEntity<?> getToolMovementsByDateRange(@PathVariable Long id, @PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        try {
            List<KardexEntity> movements = kardexService.getToolMovementsByDateRange(id, startDate, endDate);
            return ResponseEntity.ok(movements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
