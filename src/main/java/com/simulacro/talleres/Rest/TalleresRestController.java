package com.simulacro.talleres.Rest;

import com.simulacro.talleres.DAO.DAOFactory;
import com.simulacro.talleres.Entidades.Taller;
import com.simulacro.talleres.Entidades.Vehiculo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TalleresRestController {

    // Parte del get para postman tanto en talleres como en vehículos

    @GetMapping ("/api/talleres")
    List<Taller> listTalleres (){
        return DAOFactory.getInstance().getDaoTalleres().getAllTaller();
    }

    @GetMapping ("/api/vehiculos")
    List<Vehiculo> listVehiculos(){
        return DAOFactory.getInstance().getDaoTalleres().getAllVehiculo();
    }

    // Parte del post para postman en talleres y en vehiculos

    @PostMapping ("/api/vehiculos")
    Vehiculo addTaller (@RequestBody Vehiculo vehiculo) {
        DAOFactory.getInstance().getDaoTalleres().saveVehiculo(vehiculo);
        return vehiculo;
    }

    // Parte del put para postman en vehiculos

    @PutMapping("/api/vehiculos/{matricula}")
    Vehiculo updateVehiculo(@RequestBody Vehiculo vehiculo, @PathVariable String matricula){
        DAOFactory.getInstance().getDaoTalleres().updateVehiculo(matricula, vehiculo);
        return vehiculo;
    }

    // Delete para vehiculos

    @DeleteMapping ("/api/vehiculos/{matricula}")
    Vehiculo deleteVehiculo(@RequestBody Vehiculo vehiculo, @PathVariable String matricula){
        DAOFactory.getInstance().getDaoTalleres().deleteVehiculo(matricula);
        return vehiculo;
    }
}