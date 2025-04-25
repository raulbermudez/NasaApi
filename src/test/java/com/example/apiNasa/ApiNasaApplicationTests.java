package com.example.apiNasa;

import com.example.apiNasa.service.AsteroideService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ApiNasaApplicationTests {

	private AsteroideService service;
	private ObjectMapper mapper;

	@BeforeEach
	public void setup() {
		service = new AsteroideService();
		mapper = new ObjectMapper();
	}

	@Test
	public void testConstruirUrl() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		LocalDate start = LocalDate.of(2025, 4, 25);
		LocalDate end = start.plusDays(2);

		// Tengo que usar esto porque he creado el metodo privado
		Method method = service.getClass().getDeclaredMethod("construirUrl", LocalDate.class, LocalDate.class);
		method.setAccessible(true);

		String url = (String) method.invoke(service, start, end);

		assertTrue(url.contains("start_date=2025-04-25"));
		assertTrue(url.contains("end_date=2025-04-27"));
		assertTrue(url.contains("api_key="));
	}


	@Test
	public void testCalcularDiametro() throws Exception {
		ObjectNode diametros = mapper.createObjectNode();
		diametros.put("estimated_diameter_min", 0.1);
		diametros.put("estimated_diameter_max", 0.5);

		ObjectNode kilometros = mapper.createObjectNode();
		kilometros.set("kilometers", diametros);

		ObjectNode est = mapper.createObjectNode();
		est.set("estimated_diameter", kilometros);

		var method = AsteroideService.class.getDeclaredMethod("calcularDiametro", JsonNode.class);
		method.setAccessible(true);
		double result = (double) method.invoke(service, est);

		assertEquals(0.3, result, 0.0001);
	}

	@Test
	public void testObtenerTresAsteroidesMasGrandes() throws Exception {
		ArrayNode array = mapper.createArrayNode();

		for (int i = 1; i <= 5; i++) {
			ObjectNode asteroid = mapper.createObjectNode();
			asteroid.put("nombre", "Asteroide " + i);
			asteroid.put("diametro", i);
			array.add(asteroid);
		}

		var method = AsteroideService.class.getDeclaredMethod("obtenerTresAsteroidesMasGrandes", ArrayNode.class, ObjectMapper.class);
		method.setAccessible(true);
		ArrayNode top3 = (ArrayNode) method.invoke(service, array, mapper);

		assertEquals(3, top3.size());
		assertEquals("Asteroide 5", top3.get(0).get("nombre").asText());
		assertEquals("Asteroide 4", top3.get(1).get("nombre").asText());
		assertEquals("Asteroide 3", top3.get(2).get("nombre").asText());
	}

	@Test
	public void testProcesarAsteroides_agregaCorrectamente() throws Exception {
		String mockJson = """
        {
          "near_earth_objects": {
            "2025-04-25": [
              {
                "name": "TestAsteroide",
                "is_potentially_hazardous_asteroid": true,
                "estimated_diameter": {
                  "kilometers": {
                    "estimated_diameter_min": 0.2,
                    "estimated_diameter_max": 0.4
                  }
                },
                "close_approach_data": [
                  {
                    "close_approach_date": "2025-04-25",
                    "relative_velocity": {
                      "kilometers_per_hour": "12345"
                    },
                    "orbiting_body": "Earth"
                  }
                ]
              }
            ]
          }
        }""";

		JsonNode json = mapper.readTree(mockJson);
		ArrayNode resultado = mapper.createArrayNode();

		var method = AsteroideService.class.getDeclaredMethod("procesarAsteroides", JsonNode.class, ArrayNode.class);
		method.setAccessible(true);
		method.invoke(service, json, resultado);

		assertEquals(1, resultado.size());
		JsonNode asteroide = resultado.get(0);
		assertEquals("TestAsteroide", asteroide.get("nombre").asText());
		assertEquals("Earth", asteroide.get("planeta").asText());
	}
}
