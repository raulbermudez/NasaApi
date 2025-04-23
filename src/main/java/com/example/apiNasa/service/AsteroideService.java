package com.example.apiNasa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class AsteroideService {

    private static final String NASA_API_KEY = "DEMO_KEY";
    private static final String NASA_API_URL = "https://api.nasa.gov/neo/rest/v1/feed";

    public JsonNode obtenerAsteroidesPotenciales(int days) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // Para tener el dia en el que estamos
        LocalDate fecha_inicio = LocalDate.now();
        // Para sumar dias, tambien se puede sumar directamente meses y años
        LocalDate fecha_final = fecha_inicio.plusDays(days);

        String url = NASA_API_URL + "?start_date=" + fecha_inicio + "&end_date=" + fecha_final + "&api_key=" + NASA_API_KEY;

        JsonNode asteroides = null;

        try {
            String response = restTemplate.getForObject(url, String.class);
            asteroides = objectMapper.readTree(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return asteroides;
    }
}
