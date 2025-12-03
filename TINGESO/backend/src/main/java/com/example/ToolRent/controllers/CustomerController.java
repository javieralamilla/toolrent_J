package com.example.ToolRent.controllers;

import com.example.ToolRent.entities.CustomerEntity;
//Representa toda la respuesta HTTP que se envian al cliente
import com.example.ToolRent.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//Importa todas las anotaciones para mapear ENDPOINTS REST
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Convierte automaticamente los objetos Java a JSON/XML
@RestController
//Define la ruta base para todos los endpoints del controlador
@RequestMapping("/api/v1/customers")
//permite acceso desde cualquier dominio (cualquier sitio web)
@CrossOrigin("*")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    //Solo lee datos y no cambia nada del servidor
    @GetMapping("/")
    public ResponseEntity<List<CustomerEntity>> listCustomers() {
        List<CustomerEntity> customers = customerService.getCustomers();
        return ResponseEntity.ok(customers);
    }
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> getCustomerByRut(@PathVariable String rut) {
        try {
            CustomerEntity customer = customerService.getCustomerByRut(rut);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        try {
            CustomerEntity customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerEntity>> listCustomersByStatus(@PathVariable String status) {
        List<CustomerEntity> customers = customerService.getCustomerByStatus(status);
        return ResponseEntity.ok(customers);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    //El signo de interrogacion es porque te puede devolver mas de un tipo de dato
    //Requestbody: traductor que convierte Json en objeto java
    //Para crear algo nuevo
    @PostMapping("/")
    public ResponseEntity<?> saveCustomer(@RequestBody CustomerEntity customer) {
        try {
            CustomerEntity savedCustomer = customerService.saveCustomer(customer);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    //Actualizar/Modificar objeto ya existente
    @PutMapping("/")
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerEntity customer) {
        try {
            CustomerEntity customerUpdated = customerService.updateCustomer(customer);
            return ResponseEntity.ok(customerUpdated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*
    @PreAuthorize("hasAnyRole('ADMIN')")
    //@PathVariable Long id captura el valor {id} de la URL y lo convierte a Long
    //Para eliminar algo
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomerEntity> deleteCustomer(@PathVariable Long id) throws Exception {
        var isDeleted = customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    */
}
