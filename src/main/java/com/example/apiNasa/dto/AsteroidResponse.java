package com.example.apiNasa.dto;

public class AsteroidResponse {

    private String nombre;
    private double diametro;
    private String velocidad;
    private String fecha;
    private String planeta;

    public AsteroidResponse(String nombre, double diametro, String velocidad, String fecha, String planeta) {
        this.nombre = nombre;
        this.diametro = diametro;
        this.velocidad = velocidad;
        this.fecha = fecha;
        this.planeta = planeta;
    }

    // Getters y setters

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getDiameter() {
        return diametro;
    }
    public void setDiameter(double diametro) {
        this.diametro = diametro;
    }

    public String getVelocidad() {
        return velocidad;
    }
    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPlaneta() {
        return planeta;
    }

    public void setPlaneta(String planeta) {
        this.planeta = planeta;
    }
}