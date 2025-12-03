package com.example.ToolRent.repositories;

import com.example.ToolRent.entities.CustomerEntity;
//Proporciona CRUD basico automaticamente
import org.springframework.data.jpa.repository.JpaRepository;
//Permite definir consultas personalizadas, con SQL nativo

/*:nombre y :depto son placeholders con nombre
@Param("nombre") vincula el parámetro name con el placeholder :nombre
Ventaja: El orden de los parámetros ya no importa/*

 */
//Para detectar que es repositorio, igual lo detecta solo con JPArepo.. pero este da mas beneficios
import org.springframework.stereotype.Repository;

//Para usar lista
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    public Optional<CustomerEntity> findByRut(String rut);


    public List<CustomerEntity> findByStatus(String status);

    public CustomerEntity findByEmail(String email);

    public CustomerEntity findByPhoneNumber(String phoneNumber);

}
