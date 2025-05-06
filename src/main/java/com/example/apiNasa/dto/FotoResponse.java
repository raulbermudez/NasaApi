package com.example.apiNasa.dto;

public class FotoResponse {

    private String fecha;
    private String explicacion;
    private String title;
    private String url;

    public FotoResponse(String fecha, String explicacion, String title, String url) {
        this.fecha = fecha;
        this.explicacion = explicacion;
        this.title = title;
        this.url = url;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
