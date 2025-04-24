package com.example.apiNasa.controller;

import com.example.apiNasa.service.AsteroideService;
import com.fasterxml.jackson.databind.JsonNode;
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

    @GetMapping("/asteroids")
    public ResponseEntity<?> obtenerAsteroides(@RequestParam(required = false) Integer days) {
        if (days == null) {
            return ResponseEntity.badRequest().body("El parámetro 'days' es obligatorio.");
        }

        if (days < 1 || days > 7) {
            return ResponseEntity.badRequest().body("El parámetro 'days' debe estar entre 1 y 7.");
        }

        JsonNode topAsteroids = asteroidService.obtenerAsteroidesPotenciales(days);
        return ResponseEntity.ok(topAsteroids);
    }
}
