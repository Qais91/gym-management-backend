package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.CustomDietPlan;
import com.gymapp.gym_backend_service.data.model.Diets;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.dto.request.CreateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.custom_diet.UpdateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.CustomDietPlanResponseDTO;
import com.gymapp.gym_backend_service.data.dto.response.DietSummaryDTO;
import com.gymapp.gym_backend_service.data.dto.response.custom_diet_plan.DietPlanResponseDTO;
import com.gymapp.gym_backend_service.repository.CustomDietPlanRepository;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.service.CustomDietPlanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.data.dto.request.DietAssignmentRequestDTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/diet-plans")
public class CustomDietPlanController {

    @Autowired
    private CustomDietPlanService service;

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<?> getPlansByMember(@RequestHeader("Authorization") String header) {
        try {
            return ResponseEntity.ok(service.getAssignedDietPlan(header));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        }
    }

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping
    public ResponseEntity<List<DietPlanResponseDTO>> getAllDietPlans() {
        return ResponseEntity.ok(service.getAllDietPlan());
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<?> createCustomDietPlan(@RequestHeader("Authorization") String header, @Valid @RequestBody CreateDietPlanRequestDTO request) {
        try {
            return ResponseEntity.ok(new DietPlanResponseDTO(service.createCustomDietPlan(header, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/assign-diets")
    public ResponseEntity<?> assignDietsToPlan(@Valid @RequestBody DietAssignmentRequestDTO request) {
        try {
            service.assignDietToPlan(request);
            return ResponseEntity.ok(new ApiResponse("success", "Diets assigned successfully"));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PutMapping("/update-diet")
    public ResponseEntity<?> updateDietPlan(@Valid @RequestBody UpdateDietPlanRequestDTO request) {
        try {
            return ResponseEntity.ok(service.updateDietPlan(request));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        }
    }
}
