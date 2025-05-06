package com.example.apiNasa.service;

import com.example.apiNasa.dto.FotoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FotoService {

    private static final String NASA_API_KEY = "DEMO_KEY";
    private static final String NASA_API_URL = "https://api.nasa.gov/planetary/apod";

    public FotoResponse obtenerFotoActual() {
        String url = construirUrldeHoy();
        return obtenerDatos(url);
    }

    private String construirUrldeHoy() {
        // Corregimos el error en la construcción de la URL
        return NASA_API_URL + "?api_key=" + NASA_API_KEY;
    }

    private FotoResponse obtenerDatos(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            // Extraemos los campos que nos interesan
            String fecha = jsonNode.get("date").asText();
            String explicacion = jsonNode.get("explanation").asText();
            String titulo = jsonNode.get("title").asText();
            String imagenUrl = jsonNode.get("url").asText();

            return new FotoResponse(fecha, explicacion, titulo, imagenUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
