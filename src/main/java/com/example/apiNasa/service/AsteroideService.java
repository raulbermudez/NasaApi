package com.example.apiNasa.service;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        String url = construirUrl(fecha_inicio, fecha_final);

        JsonNode asteroides = obtenerDatosAsteroides(url);

        if (asteroides == null) {
            return objectMapper.createArrayNode(); // Si no se pudo obtener, retornamos un array vacío
        }

        // Creo el array que posteriormente devolvere
        ArrayNode resultado = objectMapper.createArrayNode();
        procesarAsteroides(asteroides, resultado);

        // Filtro para que solo me de los 3 asteroides con mayor diametro en el impacto
        if (resultado.size() > 3) {
            return obtenerTresAsteroidesMasGrandes(resultado, objectMapper);
        }

        return resultado;
    }

    // Construir la URL para la solicitud
    private String construirUrl(LocalDate fecha_inicio, LocalDate fecha_final) {
        return NASA_API_URL + "?start_date=" + fecha_inicio + "&end_date=" + fecha_final + "&api_key=" + NASA_API_KEY;
    }

    // Obtener los datos de los asteroides desde la API de NASA
    private JsonNode obtenerDatosAsteroides(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // En caso de error, devolvemos null
        }
    }

    // Procesar los asteroides y agregar los datos relevantes al resultado
    private void procesarAsteroides(JsonNode asteroides, ArrayNode resultado) {
        JsonNode nearEarthObjects = asteroides.path("near_earth_objects");
        ObjectMapper objectMapper = new ObjectMapper();

        for (JsonNode asteroide : nearEarthObjects) {
            for (JsonNode asteroideFinal : asteroide) {
                // Compruebo primero que sean potencialmente peligrosos
                if (asteroideFinal.get("is_potentially_hazardous_asteroid").asBoolean()) {
                    // Obtenemos los nombres de los asteroides
                    String nombre = asteroideFinal.get("name").asText();

                    ObjectNode superAsteroide = objectMapper.createObjectNode();
                    superAsteroide.put("nombre", nombre);

                    // Obtenemos el diametro de los asteroides
                    double diametro_med = calcularDiametro(asteroideFinal);
                    superAsteroide.put("diametro", diametro_med);

                    // Obtenemos la velocidad del asteroide
                    String velocidad = asteroideFinal.get("close_approach_data").get(0).get("relative_velocity")
                            .get("kilometers_per_hour").asText();
                    superAsteroide.put("velocidad", velocidad);

                    // Obtenemos la fecha
                    String fecha = asteroideFinal.get("close_approach_data").get(0).get("close_approach_date").asText();
                    superAsteroide.put("fecha", fecha);

                    // Por ultimo obtenemos el planeta
                    String planeta = asteroideFinal.get("close_approach_data").get(0).get("orbiting_body").asText();
                    superAsteroide.put("planeta", planeta);

                    resultado.add(superAsteroide);
                }
            }
        }
    }

    // Calcular el diámetro promedio de un asteroide
    private double calcularDiametro(JsonNode asteroideFinal) {
        double diametro_max = asteroideFinal.get("estimated_diameter")
                .get("kilometers")
                .get("estimated_diameter_max").asDouble();
        double diametro_min = asteroideFinal.get("estimated_diameter")
                .get("kilometers")
                .get("estimated_diameter_min")
                .asDouble();
        return (diametro_max + diametro_min) / 2;
    }

    // Filtrar y ordenar los 3 asteroides con mayor diámetro
    private ArrayNode obtenerTresAsteroidesMasGrandes(ArrayNode resultado, ObjectMapper objectMapper) {
        // Convierto el array a una lista
        List<JsonNode> listaOrdenada = new ArrayList<>();
        resultado.forEach(listaOrdenada::add);

        listaOrdenada.sort((a, b) -> {
            double diametro1 = a.get("diametro").asDouble();
            double diametro2 = b.get("diametro").asDouble();
            // Los comparo asi para que el orden sea descendente y asi luego pueda seleccionar los tres primeros del array
            return Double.compare(diametro2, diametro1);
        });

        // Creo el array de solo 3
        ArrayNode masGrandes = objectMapper.createArrayNode();
        for (int i = 0; i < 3; i++) {
            masGrandes.add((JsonNode) listaOrdenada.get(i));
        }

        return masGrandes;
    }
}