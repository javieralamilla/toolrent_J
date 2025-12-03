package com.example.ToolRent.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GlobalRates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalRatesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Nombre de la tarifa
    //En este caso solo existe una que es la tarifa diaria de multa
    @Column(unique = true, nullable = false)
    private String rateName;
    //Valor de la tarifa diaria
    @Column(unique = false, nullable = false)
    private int dailyRateValue;

}
