package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.GlobalRatesEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.repositories.GlobalRatesRepository;
import com.example.ToolRent.services.GlobalRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Convierte automaticamente los objetos Java a JSON/XML
@RestController
//Define la ruta base para todos los endpoints del controlador
@RequestMapping("/api/v1/globalRates")
//permite acceso desde cualquier dominio (cualquier sitio web)
@CrossOrigin("*")
public class GlobalRatesController {
    @Autowired
    GlobalRatesService globalRatesService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<GlobalRatesEntity>> getAllGlobalRates() {
        List<GlobalRatesEntity> rates = globalRatesService.getGlobalRates();
        return ResponseEntity.ok(rates);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findGlobalRatesById(@PathVariable Long id) {
        try {
            GlobalRatesEntity rate = globalRatesService.findGlobalRatesById(id);
            return ResponseEntity.ok(rate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<?> saveGlobalRate(@RequestBody  GlobalRatesEntity globalRatesEntity) {
        try {
            GlobalRatesEntity rate = globalRatesService.saveRate(globalRatesEntity);
            return ResponseEntity.ok(rate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}/{dailyRateValue}")
    public ResponseEntity<?> updateValueRate(@PathVariable Long id, @PathVariable int dailyRateValue ) {
        try {
            GlobalRatesEntity newRate = globalRatesService.updateValueRate(id, dailyRateValue);
            return ResponseEntity.ok(newRate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
