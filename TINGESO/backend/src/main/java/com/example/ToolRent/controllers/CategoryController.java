package com.example.ToolRent.controllers;


import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Convierte automaticamente los objetos Java a JSON/XML
@RestController
//Define la ruta base para todos los endpoints del controlador
@RequestMapping("/api/v1/categories")
//permite acceso desde cualquier dominio (cualquier sitio web)
@CrossOrigin("*")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<CategoryEntity>> ListAllCategories() {
        List<CategoryEntity> categories = categoryService.ListAllCategories();
        return ResponseEntity.ok(categories);
    }
}
