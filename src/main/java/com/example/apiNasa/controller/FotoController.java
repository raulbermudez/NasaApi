package com.example.apiNasa.controller;

import com.example.apiNasa.dto.FotoResponse;
import com.example.apiNasa.service.FotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FotoController {

    @Autowired
    FotoService fotoservice;

    @GetMapping("/foto")
    public ResponseEntity<?> obtenerFotoActual() {
        FotoResponse foto = fotoservice.obtenerFotoActual();
        if (foto == null) {
            return ResponseEntity.status(502).body("Error al obtener los datos de la NASA");
        }
        return ResponseEntity.ok(foto);
    }
}
