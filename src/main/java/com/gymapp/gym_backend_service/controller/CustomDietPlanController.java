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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.data.dto.request.DietAssignmentRequestDTO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diet-plans")
public class CustomDietPlanController {
    @Autowired
    private CustomDietPlanRepository customDietPlanRepository;
    @Autowired
    private DietsRepository dietsRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private JWTHandler jwtHandler;

    Long getMemberID(String header) {
        String token = header.substring(7);
        return jwtHandler.extractUserId(token);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<?> getPlansByMember(@RequestHeader("Authorization") String header) {
        Long validatorId = getMemberID(header);
        List<CustomDietPlan> plans = customDietPlanRepository.findByCreatedById(validatorId);
        Optional<Trainer> trainer = trainerRepository.findById(validatorId);

        if(trainer.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "Not valid trainer id"));
        if(plans.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Diet Plans so far"));

        List<DietPlanResponseDTO> result = plans.stream().map((dietPlan) -> new DietPlanResponseDTO(dietPlan)).toList();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping
    public ResponseEntity<List<DietPlanResponseDTO>> getAllDietPlans() {
        List<CustomDietPlan> customDietPlans = customDietPlanRepository.findAll();

        List<DietPlanResponseDTO> result = customDietPlans.stream().map((dietPlan) -> new DietPlanResponseDTO(dietPlan)).toList();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<?> createCustomDietPlan(@RequestHeader("Authorization") String header, @Valid @RequestBody CreateDietPlanRequestDTO request) {
        Long trainerId = getMemberID(header);
        Optional<Trainer> trainerOpt = trainerRepository.findById(trainerId);
        List<Diets> assignedDiets = (request.getDietsList() != null) ? request.getDietsList().stream().map(idVal -> {
           Optional<Diets> assignedDiet = dietsRepository.findById(Long.valueOf(idVal));
            return assignedDiet.orElse(null);
        }).toList() : null;

        if (trainerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid Trainer ID"));
        }

        CustomDietPlan plan = new CustomDietPlan();
        plan.setTitle(request.getTitle());
        plan.setCreatedBy(trainerOpt.get());
        if(assignedDiets != null) plan.setDiets(assignedDiets);

        CustomDietPlan savedPlan = customDietPlanRepository.save(plan);
        return ResponseEntity.ok(savedPlan);
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PostMapping("/assign-diets")
    public ResponseEntity<?> assignDietsToPlan(@Valid @RequestBody DietAssignmentRequestDTO request) {
        Optional<CustomDietPlan> planOpt = customDietPlanRepository.findById(request.getDietPlanId());
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Diet Plan not found"));
        }

        CustomDietPlan plan = planOpt.get();
        List<Diets> dietsToAssign = dietsRepository.findAllById(request.getDietsId());

        if (dietsToAssign.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "Diets not found enter valid diet ID")); }

        plan.setDiets(dietsToAssign);

        customDietPlanRepository.save(plan);
        return ResponseEntity.ok(new ApiResponse("success", "Diets assigned successfully"));
    }

    @PreAuthorize("hasRole('TRAINER')")
    @PutMapping("/update-diet")
    public ResponseEntity<?> updateDietPlan(@RequestBody UpdateDietPlanRequestDTO request) {
        Optional<CustomDietPlan> planOpt = customDietPlanRepository.findById(request.getDietPlanId());
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Diet Plan not found");
        }

        CustomDietPlan plan = planOpt.get();

        if (request.getNewTitle() != null && !request.getNewTitle().isBlank()) {
            plan.setTitle(request.getNewTitle());
        }

        if (request.getDietIdsToAdd() != null && !request.getDietIdsToAdd().isEmpty()) {
            List<Diets> dietsToAdd = dietsRepository.findAllById(request.getDietIdsToAdd());
            plan.getDiets().addAll(dietsToAdd);
        }

        if (request.getDietIdsToRemove() != null && !request.getDietIdsToRemove().isEmpty()) {
            plan.getDiets().removeIf(d -> request.getDietIdsToRemove().contains(d.getId()));
        }

        CustomDietPlan updatedPlan = customDietPlanRepository.save(plan);

        CustomDietPlanResponseDTO responseDTO = new CustomDietPlanResponseDTO();
        responseDTO.setId(updatedPlan.getId());
        responseDTO.setTitle(updatedPlan.getTitle());
        responseDTO.setCreatedBy(updatedPlan.getCreatedBy().getName());

        List<DietSummaryDTO> dietSummaries = updatedPlan.getDiets().stream().map(d -> {
            DietSummaryDTO dto = new DietSummaryDTO();
            dto.setId(d.getId());
            dto.setMealType(d.getMealType());
            dto.setFoodItem(d.getFoodItem());
            dto.setCalories(d.getCalories());
            return dto;
        }).toList();

        responseDTO.setDiets(dietSummaries);

        return ResponseEntity.ok(responseDTO);
    }
}
