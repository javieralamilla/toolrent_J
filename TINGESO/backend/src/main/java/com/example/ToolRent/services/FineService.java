package com.example.ToolRent.services;

import com.example.ToolRent.entities.*;
import com.example.ToolRent.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FineService {
    @Autowired
    FineRepository fineRepository;
    @Autowired
    private ToolsInventoryRepository toolsInventoryRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ToolService toolService;
    @Autowired
    private ToolRepository toolRepository;



    public ArrayList<FineEntity> getFines() {
        return (ArrayList<FineEntity>) fineRepository.findAll();
    }

    public ArrayList<FineEntity> getFinesByStatus(String status) {
        return (ArrayList<FineEntity>) fineRepository.findByStatus(status)
                .orElseThrow(() -> new IllegalArgumentException("Multas con estado " + status + " no encontrados"));
    }

    //Devuelve todos las multas de un cliente
    public ArrayList<FineEntity> findFineByCustomerRut(String rut){

        return (ArrayList<FineEntity>) fineRepository.findFineByCustomerRut(rut)
                .orElseThrow(() -> new IllegalArgumentException("Multas del cliente con rut " + rut + " no encontrados"));
    }

    //Devuelve todos las multas de un tipo
    public ArrayList<FineEntity> findByType(String type){

        return (ArrayList<FineEntity>) fineRepository.findByType(type)
                .orElseThrow(() -> new IllegalArgumentException("Multas del cliente con tipo " + type + " no encontrados"));
    }

    //Devuelve todos las multas de un cliente filtradas por estado
    public ArrayList<FineEntity> findFineByCustomerRutAndStatus(String rut, String status){

        return (ArrayList<FineEntity>) fineRepository.findFineByCustomerRutAndStatus(rut, status)
                .orElseThrow(() -> new IllegalArgumentException("Multas del cliente con rut " + rut + "y estado" + status + "no encontrados"));
    }

    //Devuelve todos las multas de un cliente filtradas por tipo
    public ArrayList<FineEntity> findFineByCustomerRutAndType(String rut, String types){

        return (ArrayList<FineEntity>) fineRepository.findFineByCustomerRutAndType(rut, types)
                .orElseThrow(() -> new IllegalArgumentException("Multas del cliente con rut " + rut + "y tipo" + types + "no encontrados"));
    }

    //Devuelve todos las multas de un cliente filtradas por tipo y estado
    public ArrayList<FineEntity> findFineByCustomerRutAndStatusAndType(String rut, String status, String types){

        return (ArrayList<FineEntity>) fineRepository.findFineByCustomerRutAndStatusAndType(rut, status, types)
                .orElseThrow(() -> new IllegalArgumentException("Multas del cliente con rut " + rut + ", estado" + status +"y tipo" + types + "no encontrados"));
    }

    // RF6.2: Listar clientes con atrasos
    public List<CustomerEntity> getCustomersWithOverdue(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return fineRepository.findCustomersWithOverdueLoansByDateRange(startDate, endDate);
        }
        return fineRepository.findCustomersWithOverdueLoans();
    }



    //Se genera una multa por atraso
    public FineEntity generateALateFine(CustomerEntity customer, LoanEntity loan, int fineValue) {
        FineEntity fine = new FineEntity();
        fine.setCustomer(customer);
        fine.setLoan(loan);
        fine.setType("atraso");
        fine.setFineValue(fineValue);
        fine.setStatus("no pagada");

        return fineRepository.save(fine);
    }

    //Se genera una multa por daño irreparable
    public FineEntity generateFineForIrreparableDamage(CustomerEntity customer, Long idLoan) {
        FineEntity fine = new FineEntity();
        LoanEntity loan = loanService.findLoanById(idLoan);
        if (loan.getStatus().equals("evaluación pendiente")) {

            //Se tiene que acceder a la tarifa de daño irreparable
            ToolEntity tool = loan.getTool();
            String categoryName = tool.getCategory().getName();

            ToolsInventoryEntity inventoryTool = toolsInventoryRepository.findByNameAndCategory(tool.getName(), categoryName);
            int rateValue = inventoryTool.getReplacementValue();

            //Se cambia el estado de la herramienta
            ToolEntity newTool = toolService.decommissionedTool(tool.getId());

            //Se cambia estado al prestamo, se actualiza la herramienta con sus nuevos estados
            loan.setTool(newTool);
            loan.setStatus("multa pendiente");
            loanRepository.save(loan);

            //Se guardan los cambios en multa
            fine.setCustomer(customer);
            fine.setLoan(loan);
            fine.setType("daño irreparable");
            fine.setFineValue(rateValue);
            fine.setStatus("no pagada");

            fineRepository.save(fine);
        }
        else{
            throw new IllegalArgumentException("El estado del prestamo no es correcto");
        }
        return fine;
    }

    //Se genera una multa por daños menores
    //fineValue es el cargo de reparacion, aplicado por el administrador en daños menores
    public FineEntity generateFineForMinorDamage(CustomerEntity customer, Long idLoan, int fineValue) {
        FineEntity fine = new FineEntity();
        LoanEntity loan = loanService.findLoanById(idLoan);
        if (loan.getStatus().equals("evaluación pendiente")) {

            //Ademas se cambia el estado del prestamo

            loan.setStatus("multa pendiente");
            loanRepository.save(loan);

            //Se guardan los cambios en multa
            fine.setCustomer(customer);
            fine.setLoan(loan);
            fine.setType("daño leve");
            fine.setFineValue(fineValue);
            fine.setStatus("no pagada");

            fineRepository.save(fine);
        }
        else{
            throw new IllegalArgumentException("El estado del prestamo no es correcto");
        }
        return fine;
    }

    // Entradas: dia de devolucion, dia actual, valor de la tarifa diaria de multa
    // Salida: el valor total de la multa hasta hoy.
    public int calculateLatePaymentPenalty(LocalDate returnDate, LocalDate today, int dailyFineRate){
         //Calcular dias de diferencia
        long daysBetween = ChronoUnit.DAYS.between(returnDate, today);

        //Lo convierto a int si es positivo
        int daysLate = (int) Math.max(0, daysBetween);

        //Calculo la multa y la retorno

        return daysLate*dailyFineRate;
    }


    public FineEntity payFine(FineEntity fine) {
        //Verifico estado multa
        if (fine.getStatus().equals("no pagada")) {
            CustomerEntity customer = fine.getCustomer();
            LoanEntity loan = fine.getLoan();
            ToolEntity tool = loan.getTool();

            //Verifico estado prestamo
            if (!loan.getStatus().equals("vencido") && !loan.getStatus().equals("activo")) {

                // 1) Verifico que el estado del prestamo sea distinto a evaluacion pendiente
                /*razon: Si ingresa aca, es poque hay una multa por atraso, pero ademas se devolvio la herramienta
                con daños, por lo tanto el estado del préstamo es: evaluacion pendiente. Por lo que mas adelante se
                generara una multa (por daños) a esta herramienta
                 */

                if (loan.getStatus().equals("evaluación pendiente")) {
                    fine.setStatus("pagada");
                    return fineRepository.save(fine);
                }

                    // 2) Verificar si el PRÉSTAMO tiene asociada otra multa
                /*
                En este caso habrian dos multas una por atraso y otra por daño (menor o irreparable)
                 */
                ArrayList<FineEntity> unpaidFines = getFinesByStatus("no pagada");

                for (FineEntity fineEntity : unpaidFines) {
                    LoanEntity loanEntity = fineEntity.getLoan();

                    //Las multas tienen que ser distintas
                    if (!fine.getId().equals(fineEntity.getId())){
                        //El prestamo asociado tiene que ser el mismo
                        if (loanEntity.getId().equals(loan.getId())){
                            fine.setStatus("pagada");
                            return fineRepository.save(fine);
                        }
                    }
                }

                // 3) Verificar si el CLIENTE tiene otra multa asociada
                for (FineEntity fineEntity : unpaidFines) {
                    CustomerEntity customerEntity = fineEntity.getCustomer();

                    //Las multas tienen que ser distintas
                    if (!fine.getId().equals(fineEntity.getId())){
                        //Los clientes tienen que ser iguales
                        if (customerEntity.getId().equals(customer.getId())) {

                            //Cambio el estado del prestamo y actualizo herramienta
                            loan.setStatus("finalizado con multa");
                            loanRepository.save(loan);

                            //Cambio el estado de la multa y actualizo variables
                            fine.setStatus("pagada");
                            fine.setLoan(loan);

                            return fineRepository.save(fine);

                        }

                    }
                }

                // 4) Verificar si Cliente tiene otros préstamos asociados a otras herramientas, con estado = ev. pendiente
                //Si ese es el caso, estado del cliente sigue en restringido

                ArrayList<LoanEntity> loansWithPendingEvaluation = loanService.findLoanByStatus("evaluación pendiente");
                for (LoanEntity loanEntity : loansWithPendingEvaluation) {
                    CustomerEntity customerEntity = loanEntity.getCustomer();

                    //Tienen que ser diferentyes prestamos
                    if (!loanEntity.getId().equals(loan.getId())){
                        //El cliente tiene que ser el mismo
                        if(customerEntity.getId().equals(customer.getId())){

                            //Cambio el estado del prestamo y actualizo herramienta
                            loan.setStatus("finalizado con multa");
                            loanRepository.save(loan);

                            //Cambio el estado de la multa y actualizo variables
                            fine.setStatus("pagada");
                            fine.setLoan(loan);

                            return fineRepository.save(fine);
                        }
                    }
                }


                /*
                5) Si llega hasta aca, es poque ninguna de las condiciones anteriores se cumplio,
                por lo que el cliente queda con estado activo
                 */

                //Cambio estado de cliente
                customer.setStatus("activo");
                customerRepository.save(customer);

                //Cambio el estado del prestamo y actualizo herramienta
                loan.setStatus("finalizado con multa");
                loanRepository.save(loan);

                //Cambio el estado de la multa y actualizo variables
                fine.setStatus("pagada");
                fine.setLoan(loan);
                fine.setCustomer(customer);

                return fineRepository.save(fine);
            }
            else {
                throw new IllegalArgumentException("El estado del prestamo no es correcto");
            }

        }
        else {
            throw new IllegalArgumentException("El estado de la multa no es correcto");
        }
    }
}