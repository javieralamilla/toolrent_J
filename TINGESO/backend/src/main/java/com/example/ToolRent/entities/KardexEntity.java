package com.example.ToolRent.entities;

import lombok.AllArgsConstructor;//Permite crear constructor vacio
import lombok.Data; //Define getter, setter, constructores
import lombok.NoArgsConstructor; //Genera constructor que acepta todos los campos de la clase como param

//Permite conectar codigo con la base de datos, sin tener que escribir SQL
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Movimientos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KardexEntity {
    @Id
    //Esto es para que la BD genere los ID automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    /*unique nos dice que el campo debe tener valores unicos
      nullable = false, el campo no puede ser nulo
     */
    @Column(unique = true, nullable = false)
    private Long id;

    //Tipos: ingreso, prestamo, devolucion, baja, reparacion
    @Column(unique = false, nullable = false)
    private String type;

    @Column(unique = false, nullable = false)
    LocalDate date;

    @Column(nullable = false)
    private String username;

    // Relacion de uno a muchos (una herramienta puede estar involucrada en muchos movmientos)
    @ManyToOne
    // define la union, es decir, define la llave foranea
    @JoinColumn
    private ToolEntity tool;

    //Cantidad afectada
    @Column(unique = false, nullable = false)
    private int affectedAmount;


}
