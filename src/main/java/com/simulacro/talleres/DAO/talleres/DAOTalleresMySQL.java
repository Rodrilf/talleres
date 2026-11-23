package com.simulacro.talleres.DAO.talleres;

import com.simulacro.talleres.Conexion.ConexionBD;
import com.simulacro.talleres.Entidades.Taller;
import com.simulacro.talleres.Entidades.Vehiculo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOTalleresMySQL implements DAOTalleres {

    // TODAS LAS CONSULTAS SQL RELACIONADAS CON TALLERES

    @Override
    public List<Taller> getAllTaller() {
        String sql = "SELECT * FROM talleres";
        List<Taller> talleres = new ArrayList<>();

        try {
            PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Taller taller = new Taller(
                        rs.getString("nombre"),
                        rs.getString("ciudad")
                );
                talleres.add(taller);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return talleres;
    }

    @Override
    public void saveTaller(Taller taller) {
        String sql = "INSERT INTO talleres (nombre, ciudad) VALUES (?, ?)";

        try {
            PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);

            ps.setString(1, taller.getNombre());
            ps.setString(2, taller.getCiudad());

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Taller updateTaller(String nombre, Taller taller) {

        String sql = "UPDATE talleres SET ciudad = ? WHERE nombre = ?";

        try {
            PreparedStatement statement = ConexionBD.getInstance().prepareStatement(sql);
            statement.setString(1, taller.getCiudad());
            statement.setString(2, nombre);
            statement.execute();

            return taller;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Taller deleteTaller(String nombre) {
        String sql = "DELETE FROM talleres WHERE nombre = ?";

        try {
            PreparedStatement statement = ConexionBD.getInstance().prepareStatement(sql);
            statement.setString(1, nombre);
            statement.execute();

            return new Taller(nombre, null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // TODAS LAS CONSULTAS SQL RELACIONADAS CON VEHÍCULOS

    @Override
    public List<Vehiculo> getAllVehiculo() {
        String sql = "SELECT * FROM vehiculos";
        List<Vehiculo> vehiculos = new ArrayList<>();

        try {
            PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vehiculo vehiculo = new Vehiculo(
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDouble("precio"),
                        rs.getString("taller")
                );
                vehiculos.add(vehiculo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vehiculos;
    }

    @Override
    public void saveVehiculo(Vehiculo vehiculo) {

        String sql = "INSERT INTO vehiculos (matricula, marca, modelo, precio, taller) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);

            ps.setString(1, vehiculo.getMatricula());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setDouble(4, vehiculo.getPrecio());
            ps.setString(5, vehiculo.getTaller());

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vehiculo updateVehiculo(String matricula, Vehiculo vehiculo) {
        String sql = "UPDATE vehiculos SET matricula = ?, marca = ?, modelo = ?, precio = ? WHERE matricula = ?";

        try {
            PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);
            ps.setString(1, vehiculo.getMatricula());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setDouble(4, vehiculo.getPrecio());
            ps.setString(5, matricula);

            ps.execute();
            return vehiculo;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vehiculo deleteVehiculo(String matricula) {
        String sql = "DELETE FROM vehiculos WHERE matricula = ?";

        try {
            PreparedStatement statement = ConexionBD.getInstance().prepareStatement(sql);
            statement.setString(1, matricula);
            statement.execute();

            return new Vehiculo(matricula, null, null, null, null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // SELECT * WHERE FUNCIONAL

    @Override
    public List<Vehiculo> filtrarVehiculo(String nombre) {
        String sql = "SELECT * FROM vehiculos WHERE taller = ?";
        List<Vehiculo> vehiculos = new ArrayList<>();

        try {
            PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vehiculo vehiculo = new Vehiculo(
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDouble("precio"),
                        rs.getString("taller")
                );
                vehiculos.add(vehiculo);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return vehiculos;
    }
}
