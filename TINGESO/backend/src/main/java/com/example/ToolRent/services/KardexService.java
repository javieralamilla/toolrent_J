package com.example.ToolRent.services;

import com.example.ToolRent.entities.KardexEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.repositories.KardexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class KardexService {
    @Autowired
    KardexRepository kardexRepository;


    //Metodos auxiliares

    //Metodo para obtener el username actual del usuario

    public String getCurrentUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt){
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaimAsString("preferred_username");
        }
        return "system";

    }

    // Función utilitaria para normalizar strings
    public String normalizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase().trim();
    }




    //----------------------------------------------------------------------------------------
    public ArrayList<KardexEntity> getAllMoves() {
        return (ArrayList<KardexEntity>) kardexRepository.findAll();
    }

    public KardexEntity registerMovement(String type, ToolEntity tool) {
        type = normalizeString(type);
        if (!type.equals("ingreso") && !type.equals("préstamo") && !type.equals("devolución") && !type.equals("baja") && !type.equals("reparación")) {
            throw  new IllegalArgumentException("El tipo de prestamo no es valido");
        }
        LocalDate today = LocalDate.now();
        String username = getCurrentUsername();

        KardexEntity kardex = new KardexEntity();
        kardex.setType(type);
        kardex.setDate(today);
        kardex.setTool(tool);
        kardex.setUsername(username);
        kardex.setAffectedAmount(1);

        return kardexRepository.save(kardex);


    }

    public List<KardexEntity> getToolMovementHistory(Long toolId) {
        if (toolId == null) {
            throw new IllegalArgumentException("La herramienta no puede ser null");
        }
        return kardexRepository.findByToolOrderByDateDesc(toolId);
    }

    public List<KardexEntity> getMovementsByDateRange(LocalDate startDate, LocalDate endDate) {
        // Validaciones
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        return kardexRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);
    }

    public List<KardexEntity> getToolMovementsByDateRange(Long toolId, LocalDate startDate, LocalDate endDate) {
        if (toolId == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        return kardexRepository.findByToolIdAndDateBetween(toolId, startDate, endDate);
    }



}
