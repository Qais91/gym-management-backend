package com.gymapp.gym_backend_service.controller;
import com.gymapp.gym_backend_service.data.dto.request.diet.CreateDietRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import com.gymapp.gym_backend_service.service.DietsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.data.model.Diets;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diets")
public class DietsController {

    @Autowired
    private DietsService service;

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<?> createDiet(@Valid @RequestBody CreateDietRequestDTO requestDiet) {
        try {
            return ResponseEntity.ok(service.createDiet(requestDiet));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('TRAINER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDietById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(service.getDietByID(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('TRAINER')")
    @GetMapping
    public ResponseEntity<?> getAllDiets() {
        try{
            return ResponseEntity.ok(service.getAllDiets());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }
}
