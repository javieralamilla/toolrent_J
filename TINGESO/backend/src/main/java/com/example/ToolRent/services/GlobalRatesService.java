package com.example.ToolRent.services;

import com.example.ToolRent.entities.GlobalRatesEntity;
import com.example.ToolRent.entities.LoanEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.repositories.GlobalRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GlobalRatesService {
    @Autowired
    private GlobalRatesRepository globalRatesRepository;


    //Metodos auxiliares

    //Metodo para validar los valores de reposicion
    private void validateDailyRateValue(Integer dailyRateValue) {
        if (dailyRateValue == null) {
            throw new IllegalArgumentException("El valor de renta no puede ser nulo");
        }
        if (dailyRateValue < 0) {
            throw new IllegalArgumentException("El valor de renta no puede ser negativo");
        }
        if (dailyRateValue < 1500) {
            throw new IllegalArgumentException(
                    String.format("El valor de renta debe ser mínimo $%,d CLP", 1500));
        }
        if (dailyRateValue > 25000) {
            throw new IllegalArgumentException(
                    String.format("El valor de renta no puede exceder $%,d CLP", 25000));
        }
    }
    //--------------------------------------------------------------------------------------------

    public GlobalRatesEntity findGlobalRatesById(Long globalRatesId) {
        return globalRatesRepository.findGlobalRatesById(globalRatesId)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa global con el id " + globalRatesId + " no encontrada"));
    }


    public GlobalRatesEntity findByRateName(String rateName) {
        return globalRatesRepository.findByRateName(rateName);
    }
    public ArrayList<GlobalRatesEntity> getGlobalRates() {
        return (ArrayList<GlobalRatesEntity>) globalRatesRepository.findAll();
    }

    public GlobalRatesEntity saveRate(GlobalRatesEntity globalRatesEntity) {
        //Se validan el valor ingresado de tarifa
        validateDailyRateValue(globalRatesEntity.getDailyRateValue());
        return globalRatesRepository.save(globalRatesEntity);
    }

    public GlobalRatesEntity updateValueRate(Long id, Integer dailyRateValue) {
        GlobalRatesEntity globalRatesEntity = globalRatesRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("tarifa con ID " + id + " no encontrada"));
        validateDailyRateValue(dailyRateValue);
        globalRatesEntity.setDailyRateValue(dailyRateValue);
        return globalRatesRepository.save(globalRatesEntity);
    }
}
