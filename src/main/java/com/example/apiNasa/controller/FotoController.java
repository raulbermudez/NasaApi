package com.example.apiNasa.controller;

import com.example.apiNasa.service.FotoService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FotoController {

    @Autowired
    FotoService fotoservice;

    // Creo la ruta de la api
    @GetMapping("/foto")
    public ResponseEntity<?> obtenerFotoActual(){
        JsonNode parametrosFoto = fotoservice.obtenerFotoActual();
        return ResponseEntity.ok(parametrosFoto);
    }
}
