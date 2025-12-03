package com.example.ToolRent.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//multas
@Entity
@Table(name = "Fines")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Relacion de uno a muchos (un cliente puede tener muchos multas)
    @ManyToOne
    //define la union, es decir, define la llave foranea
    @JoinColumn
    private CustomerEntity customer;

    //Relacion de uno a 1 (un prestamo puede estar asociado a muchas multas (max 2 una de atraso y otra por daño))
    @ManyToOne
    //define la union, es decir, define la llave foranea
    @JoinColumn
    private LoanEntity loan;

    //Tipo de multa (atraso, daño irreparable, daño leve)
    private String type;

    //El valor que lleva la multa hasta ahora
    private int fineValue;

    //Estado multa: pagada, no pagada
    private String status;
}
