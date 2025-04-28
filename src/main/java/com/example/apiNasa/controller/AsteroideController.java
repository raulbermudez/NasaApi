package com.example.apiNasa.controller;

import com.example.apiNasa.dto.AsteroidResponse;
import com.example.apiNasa.service.AsteroideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AsteroideController {

    @Autowired
    private AsteroideService asteroidService;

    // Creo la ruta de la API
    @GetMapping("/asteroids")
    public ResponseEntity<?> obtenerAsteroides(@RequestParam(required = false) Integer days) {

        // Hago dos validaciones previas a mostrar los asteroides
        if (days == null) {
            return ResponseEntity.badRequest().body("El parámetro 'days' es obligatorio.");
        }

        if (days < 1 || days > 7) {
            return ResponseEntity.badRequest().body("El parámetro 'days' debe estar entre 1 y 7.");
        }

        List<AsteroidResponse> topAsteroids = asteroidService.obtenerAsteroidesPotenciales(days);
        return ResponseEntity.ok(topAsteroids);
    }
}
