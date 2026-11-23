
package com.simulacro.talleres.Controller;

import com.simulacro.talleres.DAO.DAOFactory;
import com.simulacro.talleres.Entidades.Taller;
import com.simulacro.talleres.Entidades.Vehiculo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TalleresVehiculosController {

    // LISTADO DE TALLERES

    @GetMapping("/talleres")
    public String listarTaller(Model model) {
        model.addAttribute("taller", DAOFactory.getInstance().getDaoTalleres().getAllTaller());
        return "talleres";
    }

    // NUEVO TALLER

    @GetMapping("/nuevo")
    public String nuevo() {
        return "nuevo";
    }

    @PostMapping("/talleres/nuevo")
    public String guardarTaller(Taller taller) {
        DAOFactory.getInstance().getDaoTalleres().saveTaller(taller);
        return "redirect:/talleres";
    }

    // DETALLE DE TALLER

    @GetMapping("/talleres/{nombre}")
    public String detalle(@PathVariable String nombre, Model model) {

        for (Taller taller : DAOFactory.getInstance().getDaoTalleres().getAllTaller()) {
            if (taller.getNombre().equals(nombre)) {
                model.addAttribute("taller", taller);
            }
        }

        model.addAttribute("vehiculos", DAOFactory.getInstance().getDaoTalleres().filtrarVehiculo(nombre));

        return "detalle";
    }

    // LISTADO DE VEHICULOS

    @GetMapping("/vehiculos")
    public String listarVehiculos(Model model) {
        model.addAttribute("vehiculos", DAOFactory.getInstance().getDaoTalleres().getAllVehiculo());
        model.addAttribute("talleres", DAOFactory.getInstance().getDaoTalleres().getAllTaller());
        return "vehiculos";
    }

    // GUARDAR VEHICULO

    @PostMapping("/vehiculos/guardar")
    public String guardarVehiculo(Vehiculo vehiculo) {
        DAOFactory.getInstance().getDaoTalleres().saveVehiculo(vehiculo);
        return "redirect:/vehiculos";
    }

    // EDITAR VEHICULO

    @GetMapping("/vehiculos/editar/{matricula}")
    public String editarVehiculo(@PathVariable String matricula, Model model) {

        for (Vehiculo vehiculo : DAOFactory.getInstance().getDaoTalleres().getAllVehiculo()) {
            if (vehiculo.getMatricula().equals(matricula)) {
                model.addAttribute("vehiculo", vehiculo);
                return "editar";
            }
        }

        return "redirect:/vehiculos";
    }

    // ACTUALIZAR VEHICULO

    @PostMapping("/vehiculos/actualizar")
    public String actualizarVehiculo(Vehiculo vehiculo) {
        DAOFactory.getInstance().getDaoTalleres().updateVehiculo(vehiculo.getMatricula(), vehiculo);
        return "redirect:/vehiculos";
    }

    // ELIMINAR VEHICULO

    @PostMapping("/vehiculos/eliminar")
    public String eliminarVehiculo(String matricula) {
        DAOFactory.getInstance().getDaoTalleres().deleteVehiculo(matricula);
        return "redirect:/vehiculos";
    }
}
