package com.simulacro.talleres.Entidades;

public class Taller {
    private String nombre;
    private String ciudad;

    public Taller (String nombre, String ciudad) {
        this.nombre = nombre; // CLAVE PRIMARIA
        this.ciudad = ciudad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
