package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.CustomDietPlan;
import com.gymapp.gym_backend_service.model.Diets;
import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.dto.request.CreateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.model.dto.request.UpdateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.dto.response.CustomDietPlanResponseDTO;
import com.gymapp.gym_backend_service.model.dto.response.DietSummaryDTO;
import com.gymapp.gym_backend_service.model.dto.response.custom_diet_plan.DietPlanResponseDTO;
import com.gymapp.gym_backend_service.repository.CustomDietPlanRepository;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.model.dto.request.DietAssignmentRequestDTO;

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

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<?> getPlansByMember(@PathVariable("trainerId") Long validatorId) {
        List<CustomDietPlan> plans = customDietPlanRepository.findByCreatedById(validatorId);
        Optional<Trainer> trainer = trainerRepository.findById(validatorId);

        if(trainer.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "Not valid trainer id"));
        if(plans.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Diet Plans so far"));

        List<DietPlanResponseDTO> result = plans.stream().map((dietPlan) -> new DietPlanResponseDTO(dietPlan)).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<DietPlanResponseDTO>> getAllDietPlans() {
        List<CustomDietPlan> customDietPlans = customDietPlanRepository.findAll();

        List<DietPlanResponseDTO> result = customDietPlans.stream().map((dietPlan) -> new DietPlanResponseDTO(dietPlan)).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createCustomDietPlan(@Valid @RequestBody CreateDietPlanRequestDTO request) {
        Optional<Trainer> trainerOpt = trainerRepository.findById(request.getCreatorId());
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
