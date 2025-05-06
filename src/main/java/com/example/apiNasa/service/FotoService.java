package com.example.apiNasa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FotoService {

    private static final String NASA_API_KEY = "DEMO_KEY";
    private static final String NASA_API_URL = "https://api.nasa.gov/planetary/apod";

    public JsonNode obtenerFotoActual(){
        // RestTemplate maneja las peticiones con la Api
        RestTemplate restTemplate = new RestTemplate();
        // ObjectMapper convierte la devolución de json a objeto o de objeto a json
        ObjectMapper objectMapper = new ObjectMapper();

        String url = construirUrldeHoy();

        JsonNode foto = obtenerDatos(url);
        return foto;
    }

    private String construirUrldeHoy(){
        return NASA_API_URL + "?api_key=" + NASA_API_KEY;
    }

    private JsonNode obtenerDatos(String url){
        try{
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
