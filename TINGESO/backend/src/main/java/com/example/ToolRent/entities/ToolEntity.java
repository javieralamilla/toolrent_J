package com.example.ToolRent.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Tools")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ToolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //nullable = false, no puede ser nula la columna
    @Column(unique = false, nullable = false)
    private String name;

    @ManyToOne
    private CategoryEntity category;

    private String status;

}
