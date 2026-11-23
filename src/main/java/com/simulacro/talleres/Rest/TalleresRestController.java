package com.simulacro.talleres.Rest;

import com.simulacro.talleres.DAO.DAOFactory;
import com.simulacro.talleres.Entidades.Taller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TalleresRestController {

    @GetMapping ("/api/talleres")
    List<Taller> listTalleres (){
        return DAOFactory.getInstance().getDaoTalleres().getAllTaller();
    }
}
