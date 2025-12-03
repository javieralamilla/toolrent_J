package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.entities.FineEntity;
import com.example.ToolRent.entities.LoanEntity;
import com.example.ToolRent.entities.ToolsInventoryEntity;
import com.example.ToolRent.services.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

//Convierte automaticamente los objetos Java a JSON/XML
@RestController
//Define la ruta base para todos los endpoints del controlador
@RequestMapping("/api/v1/fines")
//permite acceso desde cualquier dominio (cualquier sitio web)
@CrossOrigin("*")

public class FineController {
    @Autowired
    private FineService fineService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<FineEntity>> listFines() {
        List<FineEntity> fines = fineService.getFines();
        return ResponseEntity.ok(fines);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getFinesByStatus(@PathVariable String status) {
        try {
            List<FineEntity> fines = fineService.getFinesByStatus(status);
            return ResponseEntity.ok(fines);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getFinesByType(@PathVariable String type) {
        try {
            List<FineEntity> fines = fineService.findByType(type);
            return ResponseEntity.ok(fines);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/customerRut/{customerRut}")
    public ResponseEntity<?> findFineByCustomerRut(@PathVariable String customerRut) {
        try {
            List<FineEntity> fines = fineService.findFineByCustomerRut(customerRut);
            return ResponseEntity.ok(fines);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/customerRut/status/{customerRut}/{status}")
    public ResponseEntity<?> findFineByCustomerRutAndStatus(@PathVariable String customerRut,  @PathVariable String status) {
        try {
            List<FineEntity> fines = fineService.findFineByCustomerRutAndStatus(customerRut, status);
            return ResponseEntity.ok(fines);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/customerRut/type/{customerRut}/{type}")
    public ResponseEntity<?> findFineByCustomerRutAndType(@PathVariable String customerRut,  @PathVariable String type) {
        try {
            List<FineEntity> fines = fineService.findFineByCustomerRutAndType(customerRut, type);
            return ResponseEntity.ok(fines);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/customerRut/status/type/{customerRut}/{status}/{type}")
    public ResponseEntity<?> findFineByCustomerRutAndStatusAndType(@PathVariable String customerRut,  @PathVariable String status, @PathVariable String type) {
        try {
            List<FineEntity> fines = fineService.findFineByCustomerRutAndStatusAndType(customerRut, status, type);
            return ResponseEntity.ok(fines);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/{idLoan}")
    public ResponseEntity<?> createFineForIrreparableDamage(@RequestBody CustomerEntity customer, @PathVariable Long idLoan) {
        try {
            FineEntity fine = fineService.generateFineForIrreparableDamage(customer, idLoan);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/minorDamage/{idLoan}/{fineValue}")
    public ResponseEntity<?> createFineForMinorDamage(@RequestBody CustomerEntity customer, @PathVariable Long idLoan, @PathVariable int fineValue) {
        try {
            FineEntity fine = fineService.generateFineForMinorDamage(customer, idLoan, fineValue);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/")
    public ResponseEntity<?> payFine(@RequestBody FineEntity fine) {
        try {
            FineEntity newFine = fineService.payFine(fine);
            return ResponseEntity.ok(newFine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF6.2: Listar clientes con atrasos
    // Uso: GET /api/v1/loans/reports/customers-with-overdue
    // Con filtro: GET /api/v1/fines/reports/customers-with-overdue?startDate=2024-01-01&endDate=2024-12-31
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/reports/customers-with-overdue")
    public ResponseEntity<?> getCustomersWithOverdue(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            List<CustomerEntity> customers = fineService.getCustomersWithOverdue(startDate, endDate);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
