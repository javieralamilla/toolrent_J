package com.example.ToolRent.entities;


import lombok.AllArgsConstructor;//Permite crear constructor vacio
import lombok.Data; //Define getter, setter, constructores
import lombok.NoArgsConstructor; //Genera constructor que acepta todos los campos de la clase como param

//Permite conectar codigo con la base de datos, sin tener que escribir SQL
import jakarta.persistence.*;

@Entity
@Table(name = "Customers")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CustomerEntity {
    @Id
    //Esto es para que la BD genere los ID automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    /*unique nos dice que el campo debe tener valores unicos
      nullable = false, el campo no puede ser nulo
     */
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String rut;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String status;

}
