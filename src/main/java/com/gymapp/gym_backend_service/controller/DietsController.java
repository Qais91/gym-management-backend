package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.repository.DietsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.model.Diets;

import java.util.List;

@RestController
@RequestMapping("/api/diets")
public class DietsController {

    @Autowired
    private DietsRepository dietsRepository;

    @PostMapping
    public ResponseEntity<Diets> createDiet(@RequestBody Diets diet) {
        return ResponseEntity.ok(dietsRepository.save(diet));
    }

    @GetMapping
    public ResponseEntity<List<Diets>> getAllDiets() {
        return ResponseEntity.ok(dietsRepository.findAll());
    }
}
