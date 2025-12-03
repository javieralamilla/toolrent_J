package com.example.ToolRent.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//prestamos
@Entity
@Table(name = "Loans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacion de uno a muchos (un cliente puede tener muchos prestamos)
    @ManyToOne
    // define la union, es decir, define la llave foranea
    @JoinColumn
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn
    private ToolEntity tool;

    //Fecha del prestamo
    private LocalDate loanDate;

    //Fecha de devolucion
    @Column(unique = false, nullable = false)
    private LocalDate returnDate;

    //estados: activo, vencido(por atraso), evaluación pendiente, multa pendiente(por daños o atraso), finalizado, finalizado con multa
    private String status;

    //Es el valor total del prestamo, si se presta 5 dias seria 5*dailyRentalRate(tarifa diaria de arriende de una herramienta)
    private int loanValue;


}
