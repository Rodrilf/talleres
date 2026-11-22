package com.simulacro.talleres.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static Connection conexion;

    private ConexionBD() {}

    public static Connection getInstance() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection("jdbc:mysql://db-test.cqhe7w9pyhsk.us-east-1.rds.amazonaws.com:3306/gestion_talleres","Rodrilf","HeUcMbYmGvEe-5633-");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conexion;
    }

    // CLONE PARA COPIAR Y PEGAR EN CLASE:
    // https://github.com/Rodrilf/talleres.git
}
