package com.example.ToolRent.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ToolsInventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolsInventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false, nullable = false)
    private String name;

    @Column(unique = false, nullable = false)
    private String category;

    //El total de herramientas (Disponible, prestadas, en reparacion)
    private int totalTools;
    //Stock disponible de herramientas
    private int currentStock;

    @Column(unique = false, nullable = false)
    private int replacementValue;

    //Tarifa diaria de arriendo de una herramienta
    @Column(unique = false, nullable = false)
    private int dailyRentalRate;

}
