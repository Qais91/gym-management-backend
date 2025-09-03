package com.gymapp.gym_backend_service.controller;
import com.gymapp.gym_backend_service.data.dto.request.diet.CreateDietRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.repository.DietsRepository;
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
    private DietsRepository dietsRepository;

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<?> createDiet(@Valid @RequestBody CreateDietRequestDTO requestDiet) {

        Diets diet = new Diets(requestDiet);

        if(requestDiet.getMealType() != null) {
            DietMealType mealType;
            try {
                mealType = DietMealType.valueOf(requestDiet.getMealType().toUpperCase());
            } catch (Exception e) {
                String mealTypes = String.join(", ",  Arrays.stream(DietMealType.values()).map(Enum::name).toList());
                return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid Meal Types. Allowed values are: " + mealTypes));
            }
            diet.setMealType(mealType);
        }

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
