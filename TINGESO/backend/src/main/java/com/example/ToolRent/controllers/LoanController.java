package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.entities.LoanEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.entities.ToolsInventoryEntity;
import com.example.ToolRent.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

//Convierte automaticamente los objetos Java a JSON/XML
@RestController
//Define la ruta base para todos los endpoints del controlador
@RequestMapping("/api/v1/loans")
//permite acceso desde cualquier dominio (cualquier sitio web)
@CrossOrigin("*")

public class LoanController {
    @Autowired
    LoanService loanService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<LoanEntity>> listAllLoans() {
        List<LoanEntity> loans = loanService.getLoans();
        return ResponseEntity.ok(loans);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findLoanById(@PathVariable Long id) {
        try {
            LoanEntity loans = loanService.findLoanById(id);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<?> findLoanByStatus(@PathVariable String status) {
        try {
            List<LoanEntity> loans = loanService.findLoanByStatus(status);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/customerRut/{rut}")
    public ResponseEntity<?> findLoanByCustomerRut(@PathVariable String rut) {
        try {
            List<LoanEntity> loans = loanService.findLoanByCustomerRut(rut);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/customerRut/status/{rut}/{status}")
    public ResponseEntity<?> findByCustomerRutAndStatus(@PathVariable String rut, @PathVariable String status) {
        try {
            List<LoanEntity> loans = loanService.findByCustomerRutAndStatus(rut, status);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/returnDate/{returnDate}")
    public ResponseEntity<?> findLoanByReturnDate(@PathVariable LocalDate returnDate) {
        try {
            List<LoanEntity> loans = loanService.findLoanByReturnDate(returnDate);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<?> createLoan(@RequestBody LoanEntity loan) {
        try {
            LoanEntity newLoan = loanService.makeLoan(loan);
            return ResponseEntity.ok(newLoan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @PutMapping("/{id}/{toolReturnStatus}")
    public ResponseEntity<?> returnLoan(@PathVariable Long id, @PathVariable String toolReturnStatus) {
        try {
            LoanEntity newLoan = loanService.loanReturn(id, toolReturnStatus);
            return ResponseEntity.ok(newLoan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ========== ENDPOINTS PARA REPORTES (ÉPICA 6) ==========

    // RF6.1: Listar préstamos activos y su estado (vigentes, atrasados)
// Uso: GET /api/v1/loans/reports/active-loans
// Con filtro: GET /api/v1/loans/reports/active-loans?startDate=2024-01-01&endDate=2024-12-31
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/reports/active-loans")
    public ResponseEntity<?> getActiveLoans(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            System.out.println("=== DEBUG ACTIVE LOANS ===");
            System.out.println("Fecha inicio recibida: " + startDate);
            System.out.println("Fecha fin recibida: " + endDate);

            List<LoanEntity> loans = loanService.getActiveLoans(startDate, endDate);

            System.out.println("Total préstamos encontrados: " + loans.size());
            for (LoanEntity loan : loans) {
                System.out.println("  ID: " + loan.getId() +
                        " | Fecha Préstamo: " + loan.getLoanDate() +
                        " | Fecha Retorno: " + loan.getReturnDate() +
                        " | Estado: " + loan.getStatus());
            }
            System.out.println("=========================");

            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // RF6.3: Reporte de las herramientas más prestadas (Ranking)
    // Uso: GET /api/v1/loans/reports/most-rented-tools
    // Con filtro: GET /api/v1/loans/reports/most-rented-tools?startDate=2024-01-01&endDate=2024-12-31
    // Retorna: Array de [toolName, categoryName, loanCount]
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/reports/most-rented-tools")
    public ResponseEntity<?> getMostRentedTools(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            List<Object[]> ranking = loanService.getMostRentedTools(startDate, endDate);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
