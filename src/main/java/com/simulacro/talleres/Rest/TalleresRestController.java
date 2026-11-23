package com.simulacro.talleres.Rest;

import com.simulacro.talleres.DAO.DAOFactory;
import com.simulacro.talleres.Entidades.Taller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class TalleresRestController {

    @GetMapping ("/api/talleres")
    List<Taller> listTalleres (){
        return DAOFactory.getInstance().getDaoTalleres().getAllTaller();
    }
}
