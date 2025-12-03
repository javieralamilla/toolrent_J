package com.example.ToolRent.services;

import com.example.ToolRent.entities.*;
import com.example.ToolRent.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.engine.transaction.internal.jta.JtaStatusHelper.isActive;

@Service
public class LoanService {
    @Autowired
    LoanRepository loanRepository;

    @Autowired
    FineRepository fineRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    ToolService toolService;

    @Autowired
    GlobalRatesService globalRatesService;


    @Autowired
    @Lazy
    FineService fineService;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ToolsInventoryRepository toolsInventoryRepository;

    @Autowired
    private KardexRepository kardexRepository;

    @Autowired
    private KardexService kardexService;

    //Metodos auxiliares


    // Función utilitaria para normalizar strings
    public String normalizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase().trim();
    }

    //Cuenta prestamos de una arreglo de prestamos
    public int countLoands(ArrayList<LoanEntity> loans){
        int count = 0;
        for(LoanEntity loan : loans){
            count++;
        }
        return count;
    }

    /*
    Entradas: lista de prestamos activos de un cliente, herramienta solicitada por cliente
    Salida: booleano, true si son la misma, false si son distintas
    Recorre toda la lista de los prestamos del cliente asociado hasta encontrar la misma herramienta solicitada
    */
    public boolean isTheSameTool(ArrayList<LoanEntity> clientActiveLoans, ToolEntity tool){

        String categoryName = tool.getCategory().getName();

        ToolsInventoryEntity toolInventory = toolsInventoryRepository.findByNameAndCategory(tool.getName(), categoryName);
        for(LoanEntity loan : clientActiveLoans){
            ToolEntity toolClient = loan.getTool();
            ToolsInventoryEntity customerToolsInventory = toolsInventoryRepository.findByNameAndCategory(toolClient.getName(), categoryName);
            if (customerToolsInventory != null){
                if (customerToolsInventory.equals(toolInventory)){
                    return true;
                }
            }
        }

        return false;
    }

    private void toolStatusIsDamaged(String toolReturnStatus, LoanEntity loan) {
        if (normalizeString(toolReturnStatus).equals("dañada")) {
            //Se cambia eestado de la herramienta a reparacion y el estado del prestamo a pendiente evaluacion
            ToolEntity newTool = toolService.damagedTool(loan.getTool().getId());

            //Se cambia estado cliente a restringido
            CustomerEntity customer = loan.getCustomer();
            customer.setStatus("restringido");
            customerRepository.save(customer);

            loan.setTool(newTool);
            loan.setStatus("evaluación pendiente");
            loan.setCustomer(customer);

            //Se hace un movimiento en el kardex DE TIPO DEVOLUCION
            KardexEntity kardex = kardexService.registerMovement("devolución", newTool);
            kardexRepository.save(kardex);

            loanRepository.save(loan);
        }
        else{
            throw new IllegalArgumentException("El estado ingresado para la herramienta a devolver no es válido");
        }
    }

    private void processToolReturn(String toolReturnStatus, LoanEntity loan) {
        if (normalizeString(toolReturnStatus).equals("buen estado")){
            //Se suma stock en el inventario y el estado a la herramienta cambia a disponible
            ToolEntity newTool = toolService.availableTool(loan.getTool().getId());
            loan.setTool(newTool);
            //Cmbio estado prestamo
            loan.setStatus("finalizado");

            //Se hace un movimiento en el kardex DE TIPO DEVOLUCION
            KardexEntity kardex = kardexService.registerMovement("devolución", newTool);
            kardexRepository.save(kardex);

            loanRepository.save(loan);
        }
        //Si la herramienta esta dañada
        else toolStatusIsDamaged(toolReturnStatus, loan);
    }

    public LoanEntity findLoanById(Long loanId) {
        return loanRepository.findLoanById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Pestamos con el id " + loanId + " no encontrado"));
    }


    //---------------------------------------------------------------------------------------------

    //Retorna todos los prestamos
    public ArrayList<LoanEntity> getLoans(){
        return (ArrayList<LoanEntity>) loanRepository.findAll();
    }

    //Retorna una lista de los prestamos y estado especifico de un cliente
    public ArrayList<LoanEntity> findByCustomerRutAndStatus(String rut, String status){

        return (ArrayList<LoanEntity>) loanRepository.findByCustomerRutAndStatus(rut, status)
                .orElseThrow(() -> new IllegalArgumentException("Pestamos con Cliente con rut " + rut + "y estado"+ status + " no encontrado"));
    }

    //Filtra prestamos por estado
    public ArrayList<LoanEntity> findLoanByStatus(String status){

        return (ArrayList<LoanEntity>) loanRepository.findByStatus(status)
                .orElseThrow(() -> new IllegalArgumentException("Prestamos con estado " + status + " no encontrados"));
    }

    //Devuelve todos los PRESTAMOS de un cliente
    public ArrayList<LoanEntity> findLoanByCustomerRut(String rut){

        return (ArrayList<LoanEntity>) loanRepository.findByCustomerRut(rut)
                .orElseThrow(() -> new IllegalArgumentException("Pestamos con cliente con rut " + rut + " no encontrados"));
    }

    //Filtra prestamos por fecha de devolucion del prestamo
    public ArrayList<LoanEntity> findLoanByReturnDate(LocalDate returnDate){

        return (ArrayList<LoanEntity>) loanRepository.findByReturnDate(returnDate)
                .orElseThrow(() -> new IllegalArgumentException("Prestamos con fecha de devolucion " + returnDate + " no encontrados"));
    }

    //Realizar un prestamo, si se cumplen todas las validaciones, retorna el prestamo
    public LoanEntity makeLoan(LoanEntity loan){
        CustomerEntity customer = loan.getCustomer();
        ToolEntity tool = loan.getTool();

        String categoryName = tool.getCategory().getName();

        // Se tienen que hacer todas las validaciones antes de realizar el prestamo

        // 1) se verifica si el estado del cliente
        if (!customerService.isActive(customer.getStatus())){
            throw new IllegalArgumentException("El cliente tiene estado restringido");

        }
        // 2) se verifica que no tenga 5 prestamos activos
        //Se obtiene la lista de los prestamos del cliente
        ArrayList<LoanEntity> clientLoans = findByCustomerRutAndStatus(customer.getRut(), "activo");

        if (countLoands(clientLoans) >= 5){
            throw new IllegalArgumentException("El cliente tiene 5 prestamos activos");
        }

        // 3) Verificar que la herramienta solicitada no sea igual a las que ya tiene prestadas

        //Se obtiene la lista de los prestamos activos de un cliente
        //Se obtienen solo los prestamos activos, xq todos los demas estados (excepto finalizados) hacen que
        //el cliente pase a estar en restringido (primera verificacion)

        ArrayList<LoanEntity> clientActiveLoans = findByCustomerRutAndStatus(customer.getRut(), "activo");

        if (isTheSameTool(clientActiveLoans, tool)){
            throw new IllegalArgumentException("El cliente ya posee un prestamo con la herramienta solicitada");
        }

        // 4) Verificar que el stock de la herramienta sea mayor a 0
        if (!toolService.hasAvailableStock(tool.getName(), categoryName)){
            throw new IllegalArgumentException("El stock de la herramienta es insuficiente");
        }

        //NO se si sea neecaria esta verificacion, xq ya esta la de prestamos con herramioemtas iguales*
        // 5) Verificar estado herramienta, solo se pueden prestar las que tienen estado activo
        if (tool.getStatus().equals("prestada")){
            throw new IllegalArgumentException("La herramienta solicitada ya esta prestada");
        }

        // 6) Verificar que la fecha de devolucion no sea anterior a la fecha del prestamo

        //Primero se pone por defecto la fecha de prestamo por la fecha actual
        loan.setLoanDate(LocalDate.now());
        //loan.setLoanDate(LocalDate.of(2025, 10, 8));
        // Verificar que returnDate no sea anterior a loanDate
        if (loan.getReturnDate().isBefore(loan.getLoanDate()) || loan.getReturnDate().isEqual(loan.getLoanDate())) {
            throw new IllegalArgumentException("La fecha de devolución no puede ser anterior o igual a la fecha de prestamo");
        }

        //Como ya se hicieron las validaciones, se procede a restar el stock y cambiar el estado de la herramienta

        ToolEntity borrowedTool = toolService.borrowedTool(tool.getId());
        loan.setTool(borrowedTool);
        //Se deja estado activo por defecto
        loan.setStatus("activo");

        //Se calcula el valor total del prestamo por todos los dias
        LocalDate today = loan.getLoanDate();
        LocalDate returnDate = loan.getReturnDate();

        ToolsInventoryEntity toolInventory = toolsInventoryRepository.findByNameAndCategory(tool.getName(), categoryName);
        int dailyRentalRate = toolInventory.getDailyRentalRate();

        //Calcular dias de diferencia
        long daysBetween = ChronoUnit.DAYS.between(today, returnDate);

        //Lo convierto a int si es positivo
        int daysLate = (int) Math.max(0, daysBetween);

        //Calculo valor prestamo
        int loanValue = daysLate*dailyRentalRate;

        loan.setLoanValue(loanValue);

        //Se hace un movimiento en el kardex DE TIPO PRESTAMO
        KardexEntity kardex = kardexService.registerMovement("préstamo", borrowedTool);
        kardexRepository.save(kardex);

        return loanRepository.save(loan);
    }

    //Solo puede existir dos estados para la herramienta devuelta (buen estado, dañada)
    public LoanEntity loanReturn(Long loanId, String toolReturnStatus){

        //Primero se busca el prestamo por el id
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo con ID " + loanId + " no encontrada"));

        //Tambien se verifica que el estado del prestamo sea activo o vencido

        if (!loan.getStatus().equals("activo") && !loan.getStatus().equals("vencido")){
            throw new IllegalArgumentException("El estado del préstamo no es válido. Solo se aceptan estados activos o vencidos para la devolución ");
        }

        //Se verifica si la fecha actaul es menor que la fecha de devolucion de la herramienta
        LocalDate today = LocalDate.now();
        if (today.isBefore(loan.getReturnDate())) {

            //Como la fecha es menor, no hay atraso, se verifica el estado de la devolucion
            processToolReturn(toolReturnStatus, loan);
        }
        // El cliente devuelve herramienta atrasada
        else {
            if (normalizeString(toolReturnStatus).equals("buen estado")){
                //Se suma stock en el inventario y el estado a la herramienta cambia a disponible
                ToolEntity newTool = toolService.availableTool(loan.getTool().getId());
                loan.setTool(newTool);
                //Cambio estado prestamo
                loan.setStatus("multa pendiente");

                //Se hace un movimiento en el kardex DE TIPO DEVOLUCION
                KardexEntity kardex = kardexService.registerMovement("devolución", newTool);
                kardexRepository.save(kardex);

                loanRepository.save(loan);
            }
            //Si la herramienta esta dañada
            else {
                toolStatusIsDamaged(toolReturnStatus, loan);
            }

        }

        return loan;
    }




    // Job que se ejecuta diariamente (usando @Scheduled)
    //@Scheduled(cron = "0 0 0 * * ?") // Todos los días a medianoche
    @Scheduled(fixedRate = 10000)
    private  void processOverdueLoans(){
        //Se obtienen todos los prestamos existentes
        ArrayList<LoanEntity> loans = getLoans();

        for (LoanEntity loan : loans) {
            //Si el prestamo esta vencido se recalcula la multa
            if (loan.getStatus().equals("vencido")) {

                // Conseguimos todas las multas no pagadas
                ArrayList<FineEntity> Fines = fineService.getFinesByStatus("no pagada");

                for (FineEntity fine : Fines) {
                    //Si los id de los prestamos son iguales, se recalcula multa
                    if (loan.getId().equals(fine.getLoan().getId())) {

                        //dia de hoy

                        LocalDate today = LocalDate.now();
                        //LocalDate today = LocalDate.of(2025, 9, 20);

                        //Buscamos el valor de la tarifa diaria de multa
                        GlobalRatesEntity dailyFineRate = globalRatesService.findByRateName("tarifa diaria de multa");
                        int dailyFineRateValue = dailyFineRate.getDailyRateValue();

                        //Se procede a calcular valor multa
                        int fineValue = fineService.calculateLatePaymentPenalty(loan.getReturnDate(), today, dailyFineRateValue);

                        //Actualizamos el valor de la multa
                        fine.setFineValue(fineValue);

                        //Se guatda multa yu prestamo
                        fineRepository.save(fine);
                        loanRepository.save(loan);

                    }
                }
            }
            //Si el prestamo esta activo, se tiene que revisar y si ya paso la fecha de devolucion
            else if (loan.getStatus().equals("activo")) {

                LocalDate today = LocalDate.now();

                //Si la fecha es actual es despues que la fecha de devolucion, entonces se tiene que crear multa
                if (today.isAfter(loan.getReturnDate())) {
                    //Buscamos el valor de la tarifa diaria de multa
                    GlobalRatesEntity dailyFineRate = globalRatesService.findGlobalRatesById(1L);
                    int dailyFineRateValue = dailyFineRate.getDailyRateValue();

                    //Se procede a calcular valor multa
                    int fineValue = fineService.calculateLatePaymentPenalty(loan.getReturnDate(), today, dailyFineRateValue);

                    //Se crea la multa
                    FineEntity newFine = fineService.generateALateFine(loan.getCustomer(), loan, fineValue);

                    //Se guarda la multa en el repositorio
                    fineRepository.save(newFine);

                    //Se cambia de estado al prestamo
                    loan.setStatus("vencido");

                    //Se cambia el estado del cliente a restringido
                    CustomerEntity customer = loan.getCustomer();
                    customer.setStatus("restringido");
                    customerRepository.save(customer);
                    loan.setCustomer(customer);

                    loanRepository.save(loan);

                }
            }

        }
    }

    // ========== MÉTODOS PARA REPORTES (ÉPICA 6) ==========

    // RF6.1: Listar préstamos activos y su estado (vigentes, atrasados)
    public List<LoanEntity> getActiveLoans(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return loanRepository.findActiveLoansByDateRange(startDate, endDate);
        }
        return loanRepository.findAllActiveLoans();
    }


    // RF6.3: Reporte de las herramientas más prestadas (Ranking)
    public List<Object[]> getMostRentedTools(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return loanRepository.findMostRentedToolsByDateRange(startDate, endDate);
        }
        return loanRepository.findMostRentedTools();
    }

}
