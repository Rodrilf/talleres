package com.simulacro.talleres.DAO;

import com.simulacro.talleres.DAO.talleres.DAOTalleres;
import com.simulacro.talleres.DAO.talleres.DAOTalleresMySQL;

public class DAOFactory {

    private static DAOFactory instance = new DAOFactory();

    private DAOTalleres daoTalleres;

    private DAOFactory(){
    }

    public static DAOFactory getInstance (){
        return instance;
    }

    public DAOTalleres getDaoTalleres() {
        if (daoTalleres == null) daoTalleres = new DAOTalleresMySQL();
        return daoTalleres;
    }
}
