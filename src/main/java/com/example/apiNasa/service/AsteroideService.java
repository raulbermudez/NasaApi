package com.example.apiNasa.service;

import com.example.apiNasa.dto.AsteroidResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AsteroideService {

    private static final String NASA_API_KEY = "DEMO_KEY";
    private static final String NASA_API_URL = "https://api.nasa.gov/neo/rest/v1/feed";

    public List<AsteroidResponse> obtenerAsteroidesPotenciales(int days) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // Calculo las fechas
        LocalDate fecha_inicio = LocalDate.now();
        LocalDate fecha_final = fecha_inicio.plusDays(days);

        String url = construirUrl(fecha_inicio, fecha_final);
        JsonNode asteroides = obtenerDatosAsteroides(url);

        if (asteroides == null) {
            return new ArrayList<>();
        }

        List<AsteroidResponse> resultado = procesarAsteroides(asteroides);

        // Si hay mas de tres asteroides en la lista, llamo a la funcion para filtrar
        if (resultado.size() > 3) {
            return obtenerTresAsteroidesMasGrandes(resultado);
        }

        return resultado;
    }

    private String construirUrl(LocalDate fecha_inicio, LocalDate fecha_final) {
        return NASA_API_URL + "?start_date=" + fecha_inicio + "&end_date=" + fecha_final + "&api_key=" + NASA_API_KEY;
    }

    // Método para obtener los asteroides
    private JsonNode obtenerDatosAsteroides(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<AsteroidResponse> procesarAsteroides(JsonNode asteroides) {
        List<AsteroidResponse> resultado = new ArrayList<>();
        JsonNode nearEarthObjects = asteroides.path("near_earth_objects");

        for (JsonNode fecha : nearEarthObjects) {
            for (JsonNode asteroide : fecha) {
                // Compruebo que sea peligroso y en caso afirmativo, obtengo todos los datos que necesito
                if (asteroide.get("is_potentially_hazardous_asteroid").asBoolean()) {
                    String nombre = asteroide.get("name").asText();
                    double diametro = calcularDiametro(asteroide);
                    String velocidad = asteroide.get("close_approach_data").get(0)
                            .get("relative_velocity").get("kilometers_per_hour").asText();
                    String fechaAproximacion = asteroide.get("close_approach_data").get(0)
                            .get("close_approach_date").asText();
                    String planeta = asteroide.get("close_approach_data").get(0)
                            .get("orbiting_body").asText();

                    // Añado el asteroide solo con los datos utiles al array creando un objeto del dto
                    resultado.add(new AsteroidResponse(nombre, diametro, velocidad, fechaAproximacion, planeta));
                }
            }
        }

        return resultado;
    }

    // Método para calcular el diametro
    private double calcularDiametro(JsonNode asteroide) {
        double diametroMax = asteroide.get("estimated_diameter").get("kilometers").get("estimated_diameter_max").asDouble();
        double diametroMin = asteroide.get("estimated_diameter").get("kilometers").get("estimated_diameter_min").asDouble();
        return (diametroMax + diametroMin) / 2;
    }

    // Método que se llamará cuando el array de asteroides tenga una longitud mayor a tres
    private List<AsteroidResponse> obtenerTresAsteroidesMasGrandes(List<AsteroidResponse> asteroides) {
        asteroides.sort(Comparator.comparingDouble(AsteroidResponse::getDiametro).reversed());
        return asteroides.subList(0, 3);
    }
}
