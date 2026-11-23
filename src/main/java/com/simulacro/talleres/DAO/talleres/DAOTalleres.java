package com.simulacro.talleres.DAO.talleres;

import com.simulacro.talleres.Entidades.Taller;
import com.simulacro.talleres.Entidades.Vehiculo;

import java.util.List;

public interface DAOTalleres {

    // MÉTODOS DAO PARA LOS TALLERES

    List<Taller> getAllTaller();
    void saveTaller (Taller taller);
    Taller updateTaller (String nombre, Taller taller);
    Taller deleteTaller (String nombre);

    // MÉTODOS PARA LOS VEHÍCULOS

    List <Vehiculo> getAllVehiculo();
    void saveVehiculo (Vehiculo vehiculo);
    Vehiculo updateVehiculo (String matricula, Vehiculo vehiculo);
    Vehiculo deleteVehiculo (String matricula);

    List <Vehiculo> filtrarVehiculo(String nombre);
}
