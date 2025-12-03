package com.example.ToolRent.services;

import com.example.ToolRent.entities.KardexEntity;
import com.example.ToolRent.entities.ToolEntity;
import com.example.ToolRent.entities.ToolsInventoryEntity;
import com.example.ToolRent.repositories.KardexRepository;
import com.example.ToolRent.repositories.ToolRepository;
import com.example.ToolRent.repositories.ToolsInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;

@Service
public class ToolService {
    @Autowired
    ToolRepository toolRepository;
    @Autowired
    ToolsInventoryRepository toolsInventoryRepository;
    @Autowired
    private KardexRepository kardexRepository;

    @Autowired
    private KardexService kardexService;
    @Autowired CategoryService categoryService;

    // SECCION FUNCIONES AUXILIARES
    // -------------------------------------------------------------------------------------

    // Función utilitaria para normalizar strings
    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase().trim();
    }

    //valida si herramienta ya esta registrada (nombre y categoria)
    public boolean isToolAlreadyRegistered(String name, String category) {
        if (toolsInventoryRepository.findByName(name) != null) {
            ToolsInventoryEntity tools = toolsInventoryRepository.findByName(name);
            return (tools.getCategory().equals(category));

        }
        return false;
    }

    //Obtener el inventario SOLO de UNA herramienta}
    //Tambien la llamo en el controlador
    public ToolsInventoryEntity getToolInventory(String name, String category) {
        name = normalizeString(name);
        ToolsInventoryEntity toolInventory = toolsInventoryRepository.findByNameAndCategory(name, category);
        if (toolInventory == null) {
            throw new IllegalArgumentException("Herramienta no encontrada en el inventario");
        }
        return toolInventory;

    }

    //Metodo para validar los valores de reposicion
    private void validateReplacementValue(Integer replacementValue) {
        if (replacementValue == null) {
            throw new IllegalArgumentException("El valor de reposición no puede ser nulo");
        }
        if (replacementValue < 0) {
            throw new IllegalArgumentException("El valor de reposición no puede ser negativo");
        }
        if (replacementValue < 2000) {
            throw new IllegalArgumentException(
                    String.format("El valor de reposición debe ser mínimo $%,d CLP", 2000));
        }
        if (replacementValue > 10000000) {
            throw new IllegalArgumentException(
                    String.format("El valor de reposición no puede exceder $%,d CLP", 10000000));
        }
    }

    //Metodo para validar los valores de reposicion
    private void validateDailyRentalRate(Integer dailyRentalRate) {
        if (dailyRentalRate == null) {
            throw new IllegalArgumentException("El valor de renta no puede ser nulo");
        }
        if (dailyRentalRate < 0) {
            throw new IllegalArgumentException("El valor de renta no puede ser negativo");
        }
        if (dailyRentalRate < 2000) {
            throw new IllegalArgumentException(
                    String.format("El valor de renta debe ser mínimo $%,d CLP", 2000));
        }
        if (dailyRentalRate > 10000000) {
            throw new IllegalArgumentException(
                    String.format("El valor de renta no puede exceder $%,d CLP", 10000000));
        }
    }


    //Verificar si hay stock disponible de una herramienta en especifico
    //Probablemente se ocupe en prestamos*
    public boolean hasAvailableStock(String name, String category) {
        try {
            ToolsInventoryEntity toolInventory = getToolInventory(name, category);
            return toolInventory.getCurrentStock() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    //-----------------------------------------------------------------------------------------------

    //Buscar herramienta por id
    public ToolEntity findToolById(Long id) {
        return toolRepository.findToolById(id);
    }

    //Listar herramientas por nombre
    public ArrayList<ToolEntity> findByName(String name) {
        name = normalizeString(name);
        return (ArrayList<ToolEntity>) toolRepository.findByName(name);
    }

    //Buscar herramienta por nombre y categoria
    public ToolEntity findToolByNameAndCategory(String name, String category) {
        name = normalizeString(name);
        return toolRepository.findToolByNameAndCategory(name, category)
                .orElse(null);  // Retorna null si no encuentra
    }

    //Listar todas las herramientas
    public ArrayList<ToolEntity> getTools() {
        return (ArrayList<ToolEntity>) toolRepository.findAll();
    }

    //Listar las herramientas segun estado
    public ArrayList<ToolEntity> getToolsByStatus(String status) {
        status = normalizeString(status);
        if (!status.equals("disponible") &&
                !status.equals("dada de baja") &&
                !status.equals("prestada") &&
                !status.equals("en reparacion")) {
            throw new IllegalArgumentException("El estado no es válido");
        }
        return (ArrayList<ToolEntity>) toolRepository.findByStatus(status);
    }

    //Listar las herramientas segun categoria
    public ArrayList<ToolEntity> getToolsByCategory(String category) {
        return (ArrayList<ToolEntity>) toolRepository.findByCategory(category);
    }

    //Listar el inventario de todas las herramientas
    public ArrayList<ToolsInventoryEntity> getToolsInventory() {
        return (ArrayList<ToolsInventoryEntity>) toolsInventoryRepository.findAll();
    }

    //Listar inventario de herramientas por categoria
    public ArrayList<ToolsInventoryEntity> findByCategory(String category) {
        return (ArrayList<ToolsInventoryEntity>) toolsInventoryRepository.findByCategory(category);
    }

    //Buscar inventario de herramienta por nombre
    public ToolsInventoryEntity findToolByName(String name) {
        return (ToolsInventoryEntity) toolsInventoryRepository.findByName(name);
    }

    //Buscar inventario de herramienta por id
    public ToolsInventoryEntity findById(Long id) {
        return (ToolsInventoryEntity) toolsInventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventario de herramienta con ID " + id + " no encontrada"));
    }

    //Se modifica el valor de reposicion
    public ToolsInventoryEntity updateReplacementValue(Long inventoryId, Integer replacementValue){
        //Se valida que sea correcto el valor de reposicion
        validateReplacementValue(replacementValue);

        ToolsInventoryEntity toolInventory = toolsInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventario de herramienta con ID " + inventoryId + " no encontrada"));
        toolInventory.setReplacementValue(replacementValue);
        toolsInventoryRepository.save(toolInventory);
        return toolInventory;

    }

    //Se modifica el valor de la tarifa de arriendo
    public ToolsInventoryEntity updateDailyRentalRate(Long inventoryId, Integer dailyRentalRate){
        //Se valida que sea correcto el valor de reposicion
        validateDailyRentalRate(dailyRentalRate);

        ToolsInventoryEntity toolInventory = toolsInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventario de herramienta con ID " + inventoryId + " no encontrada"));
        toolInventory.setDailyRentalRate(dailyRentalRate);
        toolsInventoryRepository.save(toolInventory);
        return toolInventory;

    }


    //Guarda herramientas que ya estan en el sistema
    public List<ToolEntity> saveRegisteredTool(ToolEntity tool, int quantity) {
        //Revisamos que la cantidad sea mayor a cero
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        String categoryName = tool.getCategory().getName();

        //se ve si existe la categoria en la base de datos
        if (!categoryService.isCategoryExists(categoryName)) {
            throw new IllegalArgumentException("El nombre de la categoria no existe");
        }

        //Se normalizan el nombre
        tool.setName(normalizeString(tool.getName()));

        //Veo si la herramienta ya esta registrada, ya que en ese caso se debe actualizar el stock (inventario)
        if (isToolAlreadyRegistered(tool.getName(), categoryName)) {
            //Tools son las herramientas de un mismo nombre y categoria en el inventario
            ToolsInventoryEntity inventoryTools = toolsInventoryRepository.findByNameAndCategory(tool.getName(), categoryName);

            //Se suman los stock y el total de herramientas del mismo nombre y categoria
            inventoryTools.setTotalTools(inventoryTools.getTotalTools() + quantity);
            inventoryTools.setCurrentStock(inventoryTools.getCurrentStock() + quantity);
            toolsInventoryRepository.save(inventoryTools);
        }
        //En caso de que la herramienta no este registrada
        else{
            throw new IllegalArgumentException("La herramienta ingresada no se encuentra en el sistema");
        }
        List<ToolEntity> savedTools = new ArrayList<>();

        //Se crean y guardan cada herramienta de forma individual (con id unico)
        for (int i = 0; i<quantity; i++) {
            ToolEntity newTool = new ToolEntity();
            newTool.setName(tool.getName());
            newTool.setCategory(tool.getCategory());
            newTool.setStatus("disponible");

            //Guardo en base de datos de la herramienta
            ToolEntity savedTool = toolRepository.save(newTool);
            savedTools.add(savedTool);
            //Se hace un movimiento en el kardex DE TIPO INGRESO
            KardexEntity kardex = kardexService.registerMovement("ingreso", savedTool);
            kardexRepository.save(kardex);
        }
        return savedTools;
    }

        //Guardar herramienta NO registrada en el sistema
    public List<ToolEntity> saveTool(ToolEntity tool, int quantity, int replacementValue, int dailyRentalRate) {
        //Revisamos que la cantidad sea mayor a cero
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        //Valida que el valor de reposicion sea adecuadi
        validateReplacementValue(replacementValue);
        //Valida que el valor de arriendo sea adecuado
        validateDailyRentalRate(dailyRentalRate);

        String categoryName = tool.getCategory().getName();

        //se ve si existe la categoria en la base de datos
        if (!categoryService.isCategoryExists(categoryName)) {
            throw new IllegalArgumentException("El nombre de la categoria no existe");
        }

        if (!isToolAlreadyRegistered(tool.getName(), categoryName)) {
            ToolsInventoryEntity inventoryTools = new  ToolsInventoryEntity();
            inventoryTools.setName(normalizeString(tool.getName()));
            inventoryTools.setCategory(categoryName);
            inventoryTools.setTotalTools(quantity);
            inventoryTools.setCurrentStock(quantity);
            inventoryTools.setReplacementValue(replacementValue);
            inventoryTools.setDailyRentalRate(dailyRentalRate);
            toolsInventoryRepository.save(inventoryTools);

        }
        //En caso de que la herramienta si este registrada en el sistema
        else{
            throw new IllegalArgumentException("La herramienta ingresada SI se encuentra en el sistema");
        }

        List<ToolEntity> savedTools = new ArrayList<>();

        //Se crean y guardan cada herramienta de forma individual (con id unico)
        for (int i = 0; i<quantity; i++) {
            ToolEntity newTool = new ToolEntity();
            newTool.setName(normalizeString(tool.getName()));
            newTool.setCategory(tool.getCategory());
            newTool.setStatus("disponible");

            //Guardo en base de datos de la herramienta
            ToolEntity savedTool = toolRepository.save(newTool);
            savedTools.add(savedTool);

            //Se hace un movimiento en el kardex DE TIPO INGRESO
            KardexEntity kardex = kardexService.registerMovement("ingreso", savedTool);
            kardexRepository.save(kardex);

        }

        return savedTools;
    }

    //METODOS AUXILIARES QUE SERAN USADOS EN PRESTAMOS (OTRO SERVICIO)

    // Al tener una herramienta prestada se le resta el stock y se cambia de estado a "prestada"
    public ToolEntity borrowedTool(Long toolId){
        /*BORRAR DESPUES-RECORDATORIO
        Aca no se va a ver el tema de si hay stock disponible / estado, etc, eso se ve en el objeto prestamo
        prestamo se asegura de toda esa logica de negocio, de lo unico que se encarga esta funcion es de
        restar el stock y cambiar el estado a prestada
         */

        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Herramienta con ID " + toolId + " no encontrada"));

        // Cambio del estado disponible a prestada
        tool.setStatus("prestada");

        String categoryName = tool.getCategory().getName();

        //Resto stock
        ToolsInventoryEntity toolsInventory = toolsInventoryRepository.findByNameAndCategory(tool.getName(), categoryName);
        toolsInventory.setCurrentStock(toolsInventory.getCurrentStock() - 1);

        //Se guardan los cambios hechos al inventario d ela herramienta
        toolsInventoryRepository.save(toolsInventory);
        //Se guardan los cambios hechos a la herramienta
        toolRepository.save(tool);
        return tool;

    }

    // Herramienta vuelve al estado disponible, ya sea por devolucion sin daños, o porque ya esta reparada
    // se cambia estado a disponible y se le suma el stock
    public ToolEntity availableTool(Long toolId){
        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Herramienta con ID " + toolId + " no encontrada"));
        //se cambia el estado de prestada a disponible
        tool.setStatus("disponible");

        String categoryName = tool.getCategory().getName();

        //Se aumenta el stock en 1

        ToolsInventoryEntity toolsInventory = toolsInventoryRepository.findByNameAndCategory(tool.getName(), categoryName);
        toolsInventory.setCurrentStock(toolsInventory.getCurrentStock() + 1);

        toolsInventoryRepository.save(toolsInventory);
        toolRepository.save(tool);
        return tool;
    }

    //Si una herramienta es dada de baja se le resta al stock y al total de herramientas
    //Se cambia estado a dada de baja
    public ToolEntity decommissionedTool(Long toolId){
        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Herramienta con ID " + toolId + " no encontrada"));
        //se cambia el estado a dada de baja
        tool.setStatus("dada de baja");

        String categoryName = tool.getCategory().getName();

        ToolsInventoryEntity toolsInventory = toolsInventoryRepository.findByNameAndCategory(tool.getName(), categoryName);
        toolsInventory.setTotalTools(toolsInventory.getTotalTools() - 1);
        toolsInventoryRepository.save(toolsInventory);
        toolRepository.save(tool);

        //Se hace un movimiento en el kardex DE TIPO BAJA
        KardexEntity kardex = kardexService.registerMovement("baja", tool);
        kardexRepository.save(kardex);

        return tool;

    }

    //Si la herramienta es devuelta con daños, solo se cambia el estado
    public ToolEntity damagedTool(Long toolId){
        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Herramienta con ID " + toolId + " no encontrada"));
        //se cambia el estado a dada de baja
        tool.setStatus("en reparacion");
        toolRepository.save(tool);

        //Se hace un movimiento en el kardex DE TIPO REPARACION
        KardexEntity kardex = kardexService.registerMovement("reparación", tool);
        kardexRepository.save(kardex);

        return tool;
    }

    public ToolEntity repairedTool(Long toolId){
        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Herramienta con ID " + toolId + " no encontrada"));

        if (tool.getStatus().equals("en reparacion")) {
            ToolEntity newTool = availableTool(toolId);
            toolRepository.save(newTool);
            return newTool;
        }

        throw new IllegalArgumentException("El estado de la herramiemnta no es correcto");
    }

    public List<ToolEntity> getToolsInRepairWithMinorDamageFine() {
        return toolRepository.findToolsInRepairWithMinorDamageFine();
    }

}
