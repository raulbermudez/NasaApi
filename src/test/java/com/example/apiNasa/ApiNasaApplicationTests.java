package com.example.apiNasa;

import com.example.apiNasa.dto.AsteroidResponse;
import com.example.apiNasa.service.AsteroideService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApiNasaApplicationTests {

	private AsteroideService service;
	private ObjectMapper mapper;

	@BeforeEach
	public void setup() {
		service = new AsteroideService();
		mapper = new ObjectMapper();
	}

	// Test para validar si la url se crea bien
	@Test
	public void testConstruirUrl() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		LocalDate start = LocalDate.of(2025, 4, 25);
		LocalDate end = start.plusDays(2);

		Method method = service.getClass().getDeclaredMethod("construirUrl", LocalDate.class, LocalDate.class);
		method.setAccessible(true);

		String url = (String) method.invoke(service, start, end);

		assertTrue(url.contains("start_date=2025-04-25"));
		assertTrue(url.contains("end_date=2025-04-27"));
		assertTrue(url.contains("api_key="));
	}

	// Test para validar que los datos que devuelve el calculo del diametro son correctos
	@Test
	public void testCalcularDiametro() throws Exception {
		ObjectNode diametros = mapper.createObjectNode();
		diametros.put("estimated_diameter_min", 0.1);
		diametros.put("estimated_diameter_max", 0.5);

		ObjectNode kilometros = mapper.createObjectNode();
		kilometros.set("kilometers", diametros);

		ObjectNode est = mapper.createObjectNode();
		est.set("estimated_diameter", kilometros);

		Method method = AsteroideService.class.getDeclaredMethod("calcularDiametro", JsonNode.class);
		method.setAccessible(true);
		double result = (double) method.invoke(service, est);

		assertEquals(0.3, result, 0.0001);
	}

	// Test para comprobar que me devuelve los tres asteroides con mayor diametro medio
	@Test
	public void testObtenerTresAsteroidesMasGrandes() throws Exception {
		List<AsteroidResponse> lista = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			lista.add(new AsteroidResponse("Asteroide " + i, i, "1000", "2025-04-25", "Earth"));
		}

		Method method = AsteroideService.class.getDeclaredMethod("obtenerTresAsteroidesMasGrandes", List.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		List<AsteroidResponse> top3 = (List<AsteroidResponse>) method.invoke(service, lista);

		assertEquals(3, top3.size());
		assertEquals("Asteroide 5", top3.get(0).getNombre());
		assertEquals("Asteroide 4", top3.get(1).getNombre());
		assertEquals("Asteroide 3", top3.get(2).getNombre());
	}

	// Test para comprobar que se añaden los asteroides correctamente
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

		Method method = AsteroideService.class.getDeclaredMethod("procesarAsteroides", JsonNode.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		List<AsteroidResponse> resultado = (List<AsteroidResponse>) method.invoke(service, json);

		assertEquals(1, resultado.size());
		AsteroidResponse asteroide = resultado.get(0);
		assertEquals("TestAsteroide", asteroide.getNombre());
		assertEquals("Earth", asteroide.getPlaneta());
		assertEquals(0.3, asteroide.getDiametro(), 0.0001);
	}
}
