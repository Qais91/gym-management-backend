package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.model.Diets;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diets")
public class DietsController {

    @Autowired
    private DietsRepository dietsRepository;

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<?> createDiet(@RequestBody Diets diet) {

        if(diet.getFoodItem() == null) { return ResponseEntity.ok(new ApiResponse("error", "Food Item is required")); }
        if(diet.getMealType() == null) { return ResponseEntity.ok(new ApiResponse("error", "Meal Type is mandatory field")); }
        if(diet.getCalories() == null) { return ResponseEntity.ok(new ApiResponse("error", "Calories required")); }

        if(dietsRepository.existsByFoodItem(diet.getFoodItem()) &&
                dietsRepository.existsByCalories(diet.getCalories()) &&
                    dietsRepository.existsByMealType(diet.getMealType())) {
            return ResponseEntity.ok(new ApiResponse("error", "Food with same calories available is in Menu"));
        }
        return ResponseEntity.ok(dietsRepository.save(diet));
    }

    @PreAuthorize("hasAnyRole('TRAINER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDietById(@PathVariable Long id) {
        Optional<Diets> diet = dietsRepository.findById(id);
        if(diet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("true", "No Item Found"));
        }
        return ResponseEntity.ok(diet);
    }

    @PreAuthorize("hasAnyRole('TRAINER')")
    @GetMapping
    public ResponseEntity<?> getAllDiets() {
        List<Diets> diets = dietsRepository.findAll();
        if(diets.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Diets"));
        }
        return ResponseEntity.ok(diets);
    }
}
