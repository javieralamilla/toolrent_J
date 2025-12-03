package com.example.ToolRent.services;

import com.example.ToolRent.entities.CategoryEntity;
import com.example.ToolRent.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    //Listar todas las categorias
    public ArrayList<CategoryEntity> ListAllCategories() {
        return (ArrayList<CategoryEntity>) categoryRepository.findAll();
    }

    public boolean isCategoryExists(String name) {
        return categoryRepository.findCategoryByName(name).isPresent();
    }
}
