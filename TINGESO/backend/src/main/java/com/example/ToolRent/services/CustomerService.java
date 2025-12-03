package com.example.ToolRent.services;

import com.example.ToolRent.entities.CustomerEntity;
import com.example.ToolRent.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
public class CustomerService {
    //Inyectar dependencias que se necesitan
    @Autowired
    CustomerRepository customerRepository;

    //Validar rut chilenos
    public static boolean validateRut(String rut){
        try{
            rut = rut.replace(".","").replace("-","").toUpperCase();
            if (rut.length() < 8 || rut.length() > 9) {
                return false; // no cumple con el largo válido
            }

            String body = rut.substring(0, rut.length() - 1);
            String dv =  rut.substring(rut.length() - 1);

            //Se valida que el cuerpo sean solo numeros
            if (!body.matches("\\d+")) return false;

            //Ahora se calcula digito verificador

            int sum = 0;
            int multiplier = 2;
            for (int i = body.length() - 1; i >= 0; i--) {
                sum += Integer.parseInt(String.valueOf(body.charAt(i)))*multiplier;

                //Si multiplicador llego a 7 lo reinicia a 2, si no, lo incrementa en 1.
                multiplier = multiplier == 7 ? 2 : multiplier + 1;
            }

            int remainder = sum%11;
            String dvCalculated = String.valueOf(11 - remainder);

            if (dvCalculated.equals("11")) dvCalculated = "0";
            if (dvCalculated.equals("10")) dvCalculated = "K";

            return dv.equals(dvCalculated);

        }catch (Exception e){
            return false;
        }
    }

    //Se formatea el rut para que todos los rut ingresados tenga el mismo formato
    public static String formatRut(String rut) {
        rut = rut.replace(".", "").replace("-", "").toUpperCase();
        if (rut.length() < 8 || rut.length() > 9) {
            return rut; // no cumple con el largo válido
        }

        String body = rut.substring(0, rut.length() - 1);
        String dv = rut.substring(rut.length() - 1);

        // Formatear con puntos y guión
        StringBuilder formattedRut = new StringBuilder();
        int counter = 0;

        for (int i = body.length() - 1; i >= 0; i--) {
            formattedRut.insert(0, body.charAt(i));
            counter++;
            if (counter % 3 == 0 && i > 0) {
                formattedRut.insert(0, ".");
            }
        }

        return formattedRut + "-" + dv;
    }

    // Validar email con expresión regular
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Patrón para validar formato de email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
    }

    // Validar numero de celular (chileno)
    public static boolean validateCellPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true;
        }

        // Limpiar el número
        String cleanedNumber = phone.replaceAll("[\\s\\-()]", "");


        // Verificar cada patrón específicamente
        if (cleanedNumber.matches("\\+569\\d{8}")) {
            // +56912345678 (12 caracteres: +56 + 9 + 8 dígitos)
            return false;
        } else if (cleanedNumber.matches("569\\d{8}")) {
            // 56912345678 (11 dígitos: 56 + 9 + 8 dígitos)
            return false;
        } else if (cleanedNumber.matches("9\\d{8}")) {
            // 912345678 (9 dígitos: 9 + 8 dígitos)
            return false;
        } else if (cleanedNumber.matches("\\d{8}")) {
            // 12345678 (8 dígitos puros)
            return false;
        }

        return true;
    }

    //Formatea el numero celular para que todos tengan mismo formato
    public static String formatCellPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }

        String cleanedNumber = phone.replaceAll("[\\s\\-()]", "");

        // Si tiene 11+ dígitos, probablemente tenga prefijo 56
        if (cleanedNumber.startsWith("+56") && cleanedNumber.length() > 11) {
            cleanedNumber = cleanedNumber.substring(3);
        } else if (cleanedNumber.startsWith("56") && cleanedNumber.length() > 10) {
            cleanedNumber = cleanedNumber.substring(2);
        }

        // Si tiene 8 dígitos, agrega el 9
        if (cleanedNumber.length() == 8) {
            cleanedNumber = "9" + cleanedNumber;
        }

        return "+56 " + cleanedNumber.charAt(0) + " " +
                cleanedNumber.substring(1, 5) + " " + cleanedNumber.substring(5);
    }

    //Valida que el estado sea valido y lo normaliza
    //Obtiene el string final
    public static String validateStatusTransition(CustomerEntity customer, CustomerEntity existingCustomer) {
        String currentStatus = existingCustomer.getStatus();
        String newStatus = customer.getStatus();

        // Convertir a minúsculas para comparación
        String normalizedNewStatus = newStatus.toLowerCase().trim();

        // Solo se permiten estados de activo o restringido
        if (!normalizedNewStatus.equals("activo") && !normalizedNewStatus.equals("restringido")) {
            throw new IllegalArgumentException("Estado inválido. Solo se permite 'activo' o 'restringido'");
        }

        return normalizedNewStatus;
    }

    //Metodos para validar unicidad global en rut, email, y celulares
    public boolean isRutAlreadyRegistered(String rut) {
        return customerRepository.findByRut(rut).isPresent();
    }

    public boolean isEmailAlreadyRegistered(String email) {
        return customerRepository.findByEmail(email) != null;
    }

    public boolean isPhoneAlreadyRegistered(String phone) {
        return customerRepository.findByPhoneNumber(phone) != null;
    }




    //-------------------------------------------------------------------------


    //Listar todos los clientes
    public ArrayList<CustomerEntity> getCustomers() {
        return (ArrayList<CustomerEntity>) customerRepository.findAll(); }

    //Buscar cliente por rut
    public CustomerEntity getCustomerByRut(String rut) {

        return customerRepository.findByRut(rut)
                .orElseThrow(() -> new IllegalArgumentException("Cliente con rut " + rut + " no encontrado"));
    }

    //Buscar cliente por rut
    public CustomerEntity getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente con ID " + id + " no encontrada"));
    }

    //Listar cliente por estado
    public ArrayList<CustomerEntity> getCustomerByStatus(String status) {
        return (ArrayList<CustomerEntity>) customerRepository.findByStatus(status);
    }

    //Guardar un cliente
    public CustomerEntity saveCustomer(CustomerEntity customer) {
        // Establecer estado activo por defecto
        customer.setStatus("activo");

        //se tiene que verificar si el rut es valido o no
        if (!validateRut(customer.getRut())) {
            throw new IllegalArgumentException("El rut ingresado es invalido");
        }
        //se formatea el rut para que todos tengan el mismo formato
        customer.setRut(formatRut(customer.getRut()));

        //Se verifica si este rut ya esta en la base de datos, es decir, si esta registrado
        if (isRutAlreadyRegistered(customer.getRut())) {
            throw new IllegalArgumentException("El RUT ya está registrado en el sistema");
        }

        //Se tiene que verificar si el email es valido y tambien si es que ya esta registrado
        if (!isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        if (isEmailAlreadyRegistered(customer.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado en el sistema");
        }

        //Se tiene que verificar que el numero de celular es valido y si ya esta registrado
        if (validateCellPhone(customer.getPhoneNumber())) {
            throw new IllegalArgumentException("El formato del celular no es válido");
        }

        // Se deja el mismo formato para todos los telefonos celulares
        customer.setPhoneNumber(formatCellPhone(customer.getPhoneNumber()));


        if (isPhoneAlreadyRegistered(customer.getPhoneNumber())) {
            throw new IllegalArgumentException("El telefono celular ya está registrado en el sistema");
        }


        return customerRepository.save(customer);

    }


    //Actualizar un cliente
    public CustomerEntity updateCustomer(CustomerEntity customer){
        // Verificar que el cliente existe en la base de datos
        CustomerEntity existingCustomer = customerRepository.findById(customer.getId()).orElse(null);
        if (existingCustomer == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        // Verificar que el RUT no haya sido modificado
        if (!existingCustomer.getRut().equals(customer.getRut())) {
            throw new IllegalArgumentException("No se puede modificar el RUT de un cliente existente");
        }

        //Se revisa si el estado es valido
        String normalizedNewStatus = validateStatusTransition(customer, existingCustomer);


        customer.setStatus(normalizedNewStatus);

        return customerRepository.save(customer);
    }

    /*
    //Eliminar cliente
    public boolean deleteCustomer(Long id) throws Exception {
        try {
            customerRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
     */

    //----------------------------------------------------------------------------------------------

    //METODOS AUXILIARES QUE SE OCUPAN EN OTROS SERVICIOS
    public boolean isActive (String status){
        return status.equals("activo");
    }

}
